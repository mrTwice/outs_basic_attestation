package ru.otus.basic.yampolskiy.servlets;

import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;
import ru.otus.basic.yampolskiy.webserver.interfaces.RequestHandler;

import java.lang.reflect.Method;
import java.util.Map;

public class ServletDispatcher implements RequestHandler {
    private Map<String, Method> servlets;

    public ServletDispatcher() throws Exception {
        servlets = ServletScanner.scanAndRegisterServlets("ru.otus.basic.yampolskiy.service");
    }

    @Override
    public HttpResponse execute(HttpRequest request) {
        try {
            String route = request.getRoutingKey();
            if (!servlets.containsKey(route)) {
                return new HttpResponse.Builder()
                        .setProtocolVersion(request.getProtocolVersion())
                        .setStatus(HttpStatus.NOT_FOUND)
                        .addHeader(HttpHeader.CONTENT_TYPE, "text/html")
                        .setBody("<html><body><h1>404! PAGE NOT FOUND</h1></body></html>")
                        .build();
            }
            Method method = servlets.get(route);
            Servlet servlet = (Servlet) method.getDeclaringClass().getDeclaredConstructor().newInstance();
            return (HttpResponse) method.invoke(servlet, request);
        } catch (Exception e) {
            return new HttpResponse.Builder()
                    .setProtocolVersion(request.getProtocolVersion())
                    .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .addHeader(HttpHeader.CONTENT_TYPE, "text/html")
                    .setBody("<html><body><h1>500! INTERNAL SERVER ERROR</h1></body></html>")
                    .build();
        }
    }
}
