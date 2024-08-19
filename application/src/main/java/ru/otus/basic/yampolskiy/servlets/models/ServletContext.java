package ru.otus.basic.yampolskiy.servlets.models;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServletContext {
    private static ServletContext instance;
    private Map<String, Object> attributes = new ConcurrentHashMap<>();

    private ServletContext() {}

    public static ServletContext getInstance() {
        if (instance == null) {
            instance = new ServletContext();
        }
        return instance;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }
}
