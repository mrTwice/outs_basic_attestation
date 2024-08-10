package ru.otus.basic.yampolskiy.servlets;

import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    private final HttpRequest request;
    private final Map<String, Object> attributes = new HashMap<>();

    public RequestContext(HttpRequest request) {
        this.request = request;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}
