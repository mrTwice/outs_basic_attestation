package ru.otus.basic.yampolskiy.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.filters.FilterChain;
import ru.otus.basic.yampolskiy.servlets.filters.JwtAuthenticationFilter;
import ru.otus.basic.yampolskiy.servlets.filters.PathAvailabilityFilter;
import ru.otus.basic.yampolskiy.servlets.utils.RouteMatcher;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.interfaces.RequestHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ServletDispatcher implements RequestHandler {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final RouteMatcher routeMatcher;
    private final FilterChain filterChain;

    public ServletDispatcher() throws Exception {
        Map<String, Route> routes = ServletScanner.scanAndRegisterServlets("ru.otus.basic.yampolskiy.domain.controllers");
        routeMatcher = new RouteMatcher(routes);
        filterChain = new FilterChain.Builder()
                .addFilter(new PathAvailabilityFilter(routes.keySet()))
                .addFilter(new JwtAuthenticationFilter())
                .excludePath("/signup")
                .excludePath("/signin")
                .build();
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        HttpServletRequest servletRequest = new HttpServletRequest(request);
        logger.debug("Создан объект HttpServletRequest: " + servletRequest);
        return service(servletRequest);
    }

    private HttpResponse service(HttpServletRequest request) {
        try {
            filtering(request);
            return requestProcessing(request);
        } catch (Throwable e) {
            return GlobalExceptionHandler.handleException(e, request).getHttpResponse();
        }
    }

    private boolean filtering(HttpServletRequest request) throws Throwable {
        return filterChain.doFilter(request);
    }

    private HttpResponse requestProcessing(HttpServletRequest request) throws Throwable {
        Route route = routeMatcher.match(request);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут не найден для пути запроса: " + request.getUri());
        }

        Method handlerMethod = route.getMethod();
        Object response;

        Object[] methodParameters = ParameterResolver.resolveParameters(handlerMethod, request);

        methodParameters = Arrays.stream(methodParameters)
                .filter(Objects::nonNull)
                .toArray(Object[]::new);

        boolean hasHttpServletRequest = handlerMethod.getParameterCount() > 0 &&
                handlerMethod.getParameterTypes()[0].equals(HttpServletRequest.class);

        int finalParameterCount = hasHttpServletRequest ? methodParameters.length + 1 : methodParameters.length;
        Object[] finalParameters = new Object[finalParameterCount];

        if (hasHttpServletRequest) {
            finalParameters[0] = request;
            if (methodParameters.length > 0) {
                System.arraycopy(methodParameters, 0, finalParameters, 1, methodParameters.length);
            }
        } else {
            System.arraycopy(methodParameters, 0, finalParameters, 0, methodParameters.length);
        }

        logger.debug("Фактические параметры перед вызовом метода: " + Arrays.toString(finalParameters));

        if (finalParameters.length != handlerMethod.getParameterCount()) {
            throw new IllegalArgumentException("Количество параметров не соответствует ожиданиям метода. Ожидалось: "
                    + handlerMethod.getParameterCount() + ", передано: " + finalParameters.length);
        }

        logger.debug("Вызов метода обработчика с параметрами: " + Arrays.toString(finalParameters));

        response = handlerMethod.invoke(route.getServlet(), finalParameters);

        return ((HttpServletResponse) response).getHttpResponse();
    }
}
