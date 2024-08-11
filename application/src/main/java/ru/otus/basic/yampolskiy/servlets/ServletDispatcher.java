package ru.otus.basic.yampolskiy.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.filters.FilterChain;
import ru.otus.basic.yampolskiy.servlets.filters.JwtAuthenticationFilter;
import ru.otus.basic.yampolskiy.servlets.filters.PathAvailabilityFilter;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.interfaces.RequestHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ServletDispatcher implements RequestHandler {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final Map<String, Route> routes;
    private final FilterChain filterChain;

    public ServletDispatcher() throws Exception {
        routes = new HashMap<>();
        routes.putAll(ServletScanner.scanAndRegisterServlets("ru.otus.basic.yampolskiy.domain.controllers"));
        filterChain = new FilterChain.Builder()
                .addFilter(new PathAvailabilityFilter(routes.keySet()))
                .addFilter(new JwtAuthenticationFilter())
                .excludePath("/signup")
                .excludePath("/signin")
                .build();
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        return service(new HttpServletRequest(request));
    }

    private HttpResponse service(HttpServletRequest request) {
        try {
            filtering(request);
            return requestProcessing(request);
        } catch (Throwable e) {
            return GlobalExceptionHandler.handleException(e, request).getHttpResponse();
        }
    }

    private boolean filtering(HttpServletRequest request) throws Throwable{
        return filterChain.doFilter(request);
    }

    private HttpResponse requestProcessing(HttpServletRequest request) throws Throwable {
        try {
            Route route = routes.get(request.getRoutingKey());
            return ((HttpServletResponse) route.getMethod().invoke(route.getServlet(), request)).getHttpResponse();
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

}
