package ru.otus.basic.yampolskiy.servlets.filtering;

import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;

import java.util.*;

public class FilterChain {
    private final List<Filter> filters;
    private final Set<String> excludedPaths;
    private final boolean permitAll;

    private FilterChain(List<Filter> filters, Set<String> excludedPaths, boolean permitAll) {
        this.filters = Collections.unmodifiableList(filters);
        this.excludedPaths = Collections.unmodifiableSet(excludedPaths);
        this.permitAll = permitAll;
    }

    public boolean doFilter(HttpServletRequest request) throws Exception {
        String path = request.getUri();

        if (permitAll || excludedPaths.contains(path)) {
            return true;
        }

        return executeFilters(0, request);
    }

    private boolean executeFilters(int index, HttpServletRequest request) throws Exception {
        if (index < filters.size()) {
            Filter currentFilter = filters.get(index);
            return currentFilter.doFilter(request) && executeFilters(index + 1, request);
        }
        return true;
    }

    public static class Builder {
        private final List<Filter> filters = new ArrayList<>();
        private final Set<String> excludedPaths = new HashSet<>();
        private boolean permitAll = false;

        public Builder addFilter(Filter filter) {
            filters.add(filter);
            return this;
        }

        public Builder permitAll() {
            if (!excludedPaths.isEmpty()) {
                throw new IllegalStateException("Нельзя использовать permitAll и excludePath одновременно.");
            }
            this.permitAll = true;
            return this;
        }

        public Builder excludePath(String path) {
            if (permitAll) {
                throw new IllegalStateException("Нельзя использовать permitAll и excludePath одновременно.");
            }
            excludedPaths.add(path);
            return this;
        }

        public FilterChain build() {
            return new FilterChain(new ArrayList<>(filters), new HashSet<>(excludedPaths), permitAll);
        }
    }
}



