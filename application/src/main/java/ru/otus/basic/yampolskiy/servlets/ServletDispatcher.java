package ru.otus.basic.yampolskiy.servlets;

import ru.otus.basic.yampolskiy.servlets.defaultservlets.DefaultServlet;
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
    private final Map<String, Method> routes;
    private final Method defaultMethod;
    private final Servlet defaultServlet;

    public ServletDispatcher() throws Exception {
        routes = new HashMap<>();
        routes.putAll(ServletScanner.scanAndRegisterServlets("ru.otus.basic.yampolskiy.service"));
        defaultServlet = DefaultServlet.class.getDeclaredConstructor().newInstance();
        defaultMethod = DefaultServlet.class.getDeclaredMethod("sendPageNotFound", HttpRequest.class);
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        try {
            String route = request.getRoutingKey();
            if (!routes.containsKey(route)) {
                return (HttpResponse) defaultMethod.invoke(defaultServlet, request);
            }
            return getHttpResponse(request, route);
        } catch (Exception e) {
            return new HttpResponse.Builder()
                    .setProtocolVersion(request.getProtocolVersion())
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .addHeader(HttpHeader.CONTENT_TYPE, "text/html")
                    .setBody("<html><body><h1>500! INTERNAL SERVER ERROR</h1></body></html>")
                    .build();
        }
    }

    private HttpResponse getHttpResponse(HttpRequest request, String route) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Method method = routes.get(route);
        Servlet servlet = (Servlet) method.getDeclaringClass().getDeclaredConstructor().newInstance();
        return (HttpResponse) method.invoke(servlet, request);
    }
}
