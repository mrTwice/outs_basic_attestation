package ru.otus.basic.yampolskiy.servlets;

import java.lang.reflect.Method;

public class Route {
    private final Servlet servlet;
    private final Method method;

    public Route(Servlet servlet, Method method) {
        this.servlet = servlet;
        this.method = method;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public Method getMethod() {
        return method;
    }
}
