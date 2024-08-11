package ru.otus.basic.yampolskiy.servlets.filters;

import ru.otus.basic.yampolskiy.servlets.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.exceptions.BadRequestException;

import java.util.Set;

public class PathAvailabilityFilter implements Filter {
    private final Set<String> availablePaths;

    public PathAvailabilityFilter(Set<String> availablePaths) {
        this.availablePaths = availablePaths;
    }

    @Override
    public boolean doFilter(HttpServletRequest request) throws Exception {
        String path = request.getRoutingKey();
        if (!availablePaths.contains(path)) {
            throw new BadRequestException("Запрашиваемый путь '" + path + "' не существует.");
        }
        return true;
    }
}
