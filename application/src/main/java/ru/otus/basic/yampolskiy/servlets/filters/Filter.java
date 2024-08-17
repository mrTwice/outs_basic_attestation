package ru.otus.basic.yampolskiy.servlets.filters;

import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;

public interface Filter {
    boolean doFilter(HttpServletRequest request) throws Exception;
}

