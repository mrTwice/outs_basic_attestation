package ru.otus.basic.yampolskiy.servlets;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestContext {
    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    public <T> void setAttribute(String name, T value) {
        attributes.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public void clearAttributes() {
        attributes.clear();
    }
}
