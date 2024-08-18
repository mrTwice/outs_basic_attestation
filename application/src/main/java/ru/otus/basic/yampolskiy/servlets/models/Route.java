package ru.otus.basic.yampolskiy.servlets.models;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Route {
    private final Servlet servlet;
    private final Method method;
    private final String originalPath;

    public Route(Servlet servlet, Method method, String originalPath) {
        this.servlet = servlet;
        this.method = method;
        this.originalPath = originalPath;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public Method getMethod() {
        return method;
    }

    public String getOriginalPath() {
        return originalPath;
    }

}
