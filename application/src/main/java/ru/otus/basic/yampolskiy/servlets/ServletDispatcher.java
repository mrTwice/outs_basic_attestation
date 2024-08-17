package ru.otus.basic.yampolskiy.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.exceptions.GlobalExceptionHandler;
import ru.otus.basic.yampolskiy.servlets.filters.FilterChain;
import ru.otus.basic.yampolskiy.servlets.filters.JwtAuthenticationFilter;
import ru.otus.basic.yampolskiy.servlets.filters.PathAvailabilityFilter;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.models.Route;
import ru.otus.basic.yampolskiy.servlets.routing.ServletScanner;
import ru.otus.basic.yampolskiy.servlets.routing.RequestProcessor;
import ru.otus.basic.yampolskiy.servlets.routing.RouteMatcher;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.interfaces.RequestHandler;

import java.util.Map;

public class ServletDispatcher implements RequestHandler {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final RequestProcessor requestProcessor;
    private final FilterChain filterChain;

    public ServletDispatcher() throws Exception {
        Map<String, Route> routes = ServletScanner.scanAndRegisterServlets("ru.otus.basic.yampolskiy.domain.controllers");
        RouteMatcher routeMatcher = new RouteMatcher(routes);
        this.requestProcessor = new RequestProcessor(routeMatcher);
        this.filterChain = new FilterChain.Builder()
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
            filterChain.doFilter(request);
            return requestProcessor.process(request);
        } catch (Throwable e) {
            return GlobalExceptionHandler.handleException(e, request).getHttpResponse();
        }
    }
}
