package ru.otus.basic.yampolskiy.servlets.security;

import ru.otus.basic.yampolskiy.servlets.RequestContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FilterChain {
    private List<Filter> filters = new ArrayList<>();
    private Set<String> excludedPaths = new HashSet<>();
    private boolean permitAll = false;

    public void addFilter(Filter filter) {
        filters.add(filter);
    }

    public boolean doFilter(RequestContext context) throws Exception {
        String path = context.getRequest().getUri().getPath();

        if (permitAll || excludedPaths.contains(path)) {
            return true; // Пропускаем проверку, если разрешен доступ ко всем путям или путь находится в списке исключений
        }

        return executeFilters(0, context);
    }

    private boolean executeFilters(int index, RequestContext context) throws Exception {
        if (index < filters.size()) {
            Filter currentFilter = filters.get(index);
            return currentFilter.doFilter(context) && executeFilters(index + 1, context);
        }
        return true;
    }

    public static class Builder {
        private FilterChain filterChain = new FilterChain();

        public Builder addFilter(Filter filter) {
            filterChain.addFilter(filter);
            return this;
        }

        public Builder permitAll() {
            filterChain.permitAll = true;
            return this;
        }

        public Builder excludePath(String path) {
            if (filterChain.permitAll) {
                throw new IllegalStateException("Нельзя использовать permitAll и excludePath одновременно.");
            }
            filterChain.excludedPaths.add(path);
            return this;
        }

        public FilterChain build() {
            if (filterChain.permitAll && !filterChain.excludedPaths.isEmpty()) {
                throw new IllegalStateException("Нельзя использовать permitAll и excludePath одновременно.");
            }
            return filterChain;
        }
    }
}



