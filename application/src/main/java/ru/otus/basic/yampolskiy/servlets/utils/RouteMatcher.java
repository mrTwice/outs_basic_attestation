package ru.otus.basic.yampolskiy.servlets.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.Route;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteMatcher {
    private static final Logger logger = LogManager.getLogger(RouteMatcher.class);

    private final Map<String, Route> routes;

    public RouteMatcher(Map<String, Route> routes) {
        this.routes = routes;
    }

    public Route match(HttpServletRequest request) {
        String requestPath = request.getUri(); // Получаем URI запроса
        String requestMethod = request.getMethod().toString(); // Получаем метод запроса

        // Формируем ключ в формате "METHOD /path"
        String routeKey = requestMethod + " " + requestPath;

        logger.debug("Попытка найти маршрут для пути '{}' и метода '{}'", requestPath, requestMethod);

        for (Map.Entry<String, Route> entry : routes.entrySet()) {
            if (matches(entry.getKey(), routeKey, entry.getValue(), request)) {
                logger.debug("Найден маршрут: {}", entry.getValue());
                return entry.getValue();
            }
        }

        logger.debug("Маршрут не найден для пути '{}' и метода '{}'", requestPath, requestMethod);
        return null; // если соответствия не найдено
    }

    private boolean matches(String routeKey, String requestRouteKey, Route route, HttpServletRequest request) {
        String[] parts = routeKey.split(" ", 2);

        if (parts.length != 2) {
            logger.warn("Некорректный ключ маршрута '{}'. Он должен содержать метод и путь.", routeKey);
            return false;
        }

        String method = parts[0];
        String pathPattern = parts[1];

        if (!requestRouteKey.startsWith(method)) {
            logger.debug("Метод не совпадает. Ожидался: '{}', но найден: '{}'", method, request.getMethod());
            return false;
        }

        return pathMatches(route, pathPattern, request);
    }

    private boolean pathMatches(Route route, String pathPattern, HttpServletRequest request) {
        String regex = pathPattern
                .replaceAll("\\{([^/]+)}", "([^/]+)") // Переменные пути
                .replaceAll("\\*", ".*"); // Поддержка символа *

        logger.debug("Преобразованный шаблон пути в регулярное выражение: {}", regex);

        Pattern pattern = Pattern.compile("^" + regex + "$");
        Matcher matcher = pattern.matcher(request.getUri());

        if (matcher.matches()) {
            logger.debug("Совпадение найдено для пути: {}", request.getUri());
            extractPathVariables(matcher, route.getOriginalPath(), request);
            return true;
        } else {
            logger.debug("Нет совпадения для шаблона пути '{}' и пути запроса '{}'", regex, request.getUri());
            return false;
        }
    }

    private void extractPathVariables(Matcher matcher, String originalPathPattern, HttpServletRequest request) {
        String[] patternParts = originalPathPattern.split("/");
        int groupIndex = 1;

        for (String patternPart : patternParts) {
            if (patternPart.startsWith("{") && patternPart.endsWith("}")) {
                String variableName = patternPart.substring(1, patternPart.length() - 1);
                if (groupIndex <= matcher.groupCount()) {
                    String variableValue = matcher.group(groupIndex++);
                    request.getRequestContext().setAttribute(variableName, variableValue);
                    logger.debug("Извлеченная переменная пути: {} = {}", variableName, variableValue);
                }
            }
        }
    }
}



