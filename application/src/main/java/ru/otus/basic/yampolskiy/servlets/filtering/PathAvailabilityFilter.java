package ru.otus.basic.yampolskiy.servlets.filtering;

import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.exceptions.BadRequestException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class PathAvailabilityFilter implements Filter {
    private final Set<String> availablePaths;
    private final Map<String, Pattern> regexCache = new ConcurrentHashMap<>();

    public PathAvailabilityFilter(Set<String> availablePaths) {
        this.availablePaths = availablePaths;
    }

    @Override
    public boolean doFilter(HttpServletRequest request) throws Exception {
        String path = request.getRoutingKey();

        // Проверка пути по регулярному выражению
        boolean pathExists = availablePaths.stream()
                .anyMatch(availablePath -> pathMatches(availablePath, path));

        if (!pathExists) {
            throw new BadRequestException("Запрашиваемый путь '" + path + "' не существует.");
        }

        return true;
    }

    private boolean pathMatches(String pathPattern, String requestPath) {
        Pattern pattern = regexCache.computeIfAbsent(pathPattern, p ->
                Pattern.compile(p.replaceAll("\\{[^/]+\\}", "[^/]+"))
        );
        return pattern.matcher(requestPath).matches();
    }
}
