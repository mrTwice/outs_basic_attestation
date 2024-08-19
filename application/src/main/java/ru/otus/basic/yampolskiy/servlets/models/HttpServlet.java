package ru.otus.basic.yampolskiy.servlets.models;

public class HttpServlet implements Servlet {
    protected ServletContext context;

    public HttpServlet() {
        this.context = ServletContext.getInstance();
    }

    public ServletContext getContext() {
        return context;
    }

}
