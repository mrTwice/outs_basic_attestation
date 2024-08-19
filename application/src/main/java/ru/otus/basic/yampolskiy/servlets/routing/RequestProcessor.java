package ru.otus.basic.yampolskiy.servlets.routing;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletResponse;
import ru.otus.basic.yampolskiy.servlets.models.Route;
import ru.otus.basic.yampolskiy.servlets.models.Servlet;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class RequestProcessor {
    private final Logger logger = LogManager.getLogger(RequestProcessor.class);
    private final RouteMatcher routeMatcher;

    public RequestProcessor(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }

    public HttpResponse process(HttpServletRequest request) throws Throwable {
        Route route = findRoute(request);
        Method targetMethod = route.getMethod();

        Object[] methodParameters = prepareParametersIfNeeded(targetMethod, request);
        validateParameters(targetMethod, methodParameters);

        Object response = invokeHandler(route.getServlet(), targetMethod, methodParameters);
        return getHttpResponse(response);
    }

    private Object[] prepareParametersIfNeeded(Method targetMethod, HttpServletRequest request) throws Throwable {
        // Если метод не требует параметров, возвращаем пустой массив
        if (targetMethod.getParameterCount() == 0) {
            return new Object[0];
        }

        // Подготовка параметров, если они требуются
        return prepareParameters(targetMethod, request);
    }

    private Route findRoute(HttpServletRequest request) {
        Route route = routeMatcher.match(request);
        if (route == null) {
            throw new IllegalArgumentException("Маршрут не найден для пути запроса: " + request.getUri());
        }
        return route;
    }

    private Object[] prepareParameters(Method targetMethod, HttpServletRequest request) throws Throwable {
        Object[] methodParameters = ParameterResolver.resolveParameters(targetMethod, request);

        methodParameters = Arrays.stream(methodParameters)
                .filter(Objects::nonNull)
                .toArray(Object[]::new);

        if (targetMethod.getParameterCount() > 0 &&
                targetMethod.getParameterTypes()[0].equals(HttpServletRequest.class)) {
            Object[] finalParameters = new Object[methodParameters.length + 1];
            finalParameters[0] = request;
            System.arraycopy(methodParameters, 0, finalParameters, 1, methodParameters.length);
            return finalParameters;
        } else {
            return methodParameters;
        }
    }

    private void validateParameters(Method targetMethod, Object[] parameters) {
        if (parameters.length != targetMethod.getParameterCount()) {
            throw new IllegalArgumentException("Количество параметров не соответствует ожиданиям метода. Ожидалось: "
                    + targetMethod.getParameterCount() + ", передано: " + parameters.length);
        }
    }

    private Object invokeHandler(Servlet servlet, Method targetMethod, Object[] parameters) throws Throwable {
        logger.debug("Вызов метода обработчика с параметрами: " + Arrays.toString(parameters));

        try {
            return targetMethod.invoke(servlet, parameters);
        } catch (InvocationTargetException e) {
            // Пробрасываем оригинальное исключение, вызванное методом
            throw e.getCause();
        }
    }

    private HttpResponse getHttpResponse(Object response) {
        if (!(response instanceof HttpServletResponse)) {
            throw new IllegalStateException("Ответ должен быть экземпляром HttpServletResponse");
        }
        return ((HttpServletResponse) response).getHttpResponse();
    }
}

