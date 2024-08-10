package ru.otus.basic.yampolskiy.servlets.security;

import ru.otus.basic.yampolskiy.servlets.RequestContext;

public interface Filter {
    boolean doFilter(RequestContext context) throws Exception;
}

