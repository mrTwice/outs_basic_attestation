package ru.otus.basic.yampolskiy.servlets;

public abstract class HttpServlet implements Servlet {
    protected ServletContext context;

    public HttpServlet() {
        this.context = ServletContext.getInstance();
    }

    public ServletContext getContext() {
        return context;
    }

}
