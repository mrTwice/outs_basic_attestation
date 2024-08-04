package ru.otus.basic.yampolskiy.webserver.interfaces;

import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;

public interface RequestHandler {
    HttpResponse execute(HttpRequest request);
}
