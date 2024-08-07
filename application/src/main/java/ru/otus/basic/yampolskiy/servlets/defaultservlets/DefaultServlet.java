package ru.otus.basic.yampolskiy.servlets.defaultservlets;

import ru.otus.basic.yampolskiy.servlets.HttpServlet;
import ru.otus.basic.yampolskiy.servlets.annotations.GetRoute;
import ru.otus.basic.yampolskiy.servlets.annotations.WebServlet;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;

@WebServlet("/")
public class DefaultServlet extends HttpServlet {

    @GetRoute
    public HttpResponse sendPageNotFound(HttpRequest request) {
        return new HttpResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.NOT_FOUND)
                .addHeader(HttpHeader.CONTENT_TYPE, "text/html")
                .setBody("<html><body><h1>404! PAGE NOT FOUND</h1></body></html>")
                .build();
    }

    @GetRoute
    public HttpResponse sendInternalServerError(HttpRequest request) {
        return new HttpResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .addHeader(HttpHeader.CONTENT_TYPE, "text/html")
                .setBody("<html><body><h1>500! INTERNAL SERVER ERROR</h1></body></html>")
                .build();
    }
}
