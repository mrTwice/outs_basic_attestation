package ru.otus.basic.yampolskiy.servlets;

import java.util.HashMap;
import java.util.Map;

public class RequestContext {
    private final Map<String, Object> attributes = new HashMap<>();

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }
}
