package ru.otus.basic.yampolskiy.servlets.security;

import ru.otus.basic.yampolskiy.servlets.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.RequestContext;

public interface Filter {
    boolean doFilter(HttpServletRequest request) throws Exception;
}

