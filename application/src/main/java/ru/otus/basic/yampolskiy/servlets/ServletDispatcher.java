package ru.otus.basic.yampolskiy.servlets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.defaultservlets.DefaultServlet;
import ru.otus.basic.yampolskiy.servlets.security.FilterChain;
import ru.otus.basic.yampolskiy.servlets.security.JwtAuthenticationFilter;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;
import ru.otus.basic.yampolskiy.webserver.interfaces.RequestHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ServletDispatcher implements RequestHandler {
    private final Logger logger = LogManager.getLogger(this.getClass().getName());
    private final Map<String, Method> routes;
    private final Method defaultMethod;
    private final Servlet defaultServlet;
    private final FilterChain filterChain;

    public ServletDispatcher() throws Exception {
        routes = new HashMap<>();
        routes.putAll(ServletScanner.scanAndRegisterServlets("ru.otus.basic.yampolskiy.domain.controllers"));
        defaultServlet = DefaultServlet.class.getDeclaredConstructor().newInstance();
        defaultMethod = DefaultServlet.class.getDeclaredMethod("sendPageNotFound", HttpRequest.class);
        filterChain = new FilterChain.Builder()
                .addFilter(new JwtAuthenticationFilter()) // Фильтр аутентификации
                //.permitAll() // Опция для разрешения доступа ко всем путям без проверки
                .excludePath("/signup")
                .excludePath("/signin")
                .build();
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        RequestContext context = new RequestContext(request);
        try {
            if (!filterChain.doFilter(context)) {
                return new HttpResponse.Builder()
                        .setProtocolVersion(request.getProtocolVersion())
                        .setStatus(HttpStatus.UNAUTHORIZED)
                        .addHeader(HttpHeader.CONTENT_TYPE, "text/html")
                        .setBody("<html><body><h1>401! UNAUTHORIZED</h1></body></html>")
                        .build();
            }
            String route = request.getRoutingKey();
            if (!routes.containsKey(route)) {
                return (HttpResponse) defaultMethod.invoke(defaultServlet, request);
            }
            return getHttpResponse(request, route);
        } catch (Exception e) {
            logger.error("Ошибка обработки запроса",e);
            return new HttpResponse.Builder()
                    .setProtocolVersion(request.getProtocolVersion())
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .addHeader(HttpHeader.CONTENT_TYPE, "text/html")
                    .setBody("<html><body><h1>500! INTERNAL SERVER ERROR</h1></body></html>")
                    .build();
        }
    }

    private HttpResponse getHttpResponse(HttpRequest request, String route) throws Exception {
        Method method = routes.get(route);
        Servlet servlet = (Servlet) method.getDeclaringClass().getDeclaredConstructor().newInstance();
        try {
            return (HttpResponse) method.invoke(servlet, request);
        } catch (InvocationTargetException e) {
            logger.error("Ошибка внутри метода сервлета", e.getCause());
            throw e;
        }
    }
}
