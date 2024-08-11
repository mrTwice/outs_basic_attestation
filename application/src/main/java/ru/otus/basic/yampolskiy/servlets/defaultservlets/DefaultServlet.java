package ru.otus.basic.yampolskiy.servlets.defaultservlets;

import ru.otus.basic.yampolskiy.servlets.HttpServlet;
import ru.otus.basic.yampolskiy.servlets.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.HttpServletResponse;
import ru.otus.basic.yampolskiy.servlets.annotations.GetRoute;
import ru.otus.basic.yampolskiy.servlets.annotations.WebServlet;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;

@WebServlet("/")
public class DefaultServlet extends HttpServlet {

    @GetRoute
    public HttpServletResponse sendPageNotFound(HttpServletRequest request) {
        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(HttpStatus.NOT_FOUND)
                .addHeader(HttpHeader.CONTENT_TYPE, "text/html")
                .setBody("<html><body><h1>404! PAGE NOT FOUND</h1></body></html>")
                .build();
    }
}
