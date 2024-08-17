package ru.otus.basic.yampolskiy.servlets.models;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class Route {
    private final Servlet servlet;
    private final Method method;
    private final String originalPath;

    public Route(Servlet servlet, Method method, String originalPath) {
        this.servlet = servlet;
        this.method = method;
        this.originalPath = originalPath;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public Method getMethod() {
        return method;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public Parameter[] getParameters() {
        return method.getParameters();
    }

    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    public Annotation[][] getParameterAnnotations() {
        return method.getParameterAnnotations();
    }

    public Map<String, String> extractPathVariables(String requestPath) {
        String[] originalParts = originalPath.split("/");
        String[] requestParts = requestPath.split("/");
        Map<String, String> variables = new HashMap<>();

        if (originalParts.length != requestParts.length) {
            throw new IllegalArgumentException("Путь запроса не совпадает с шаблоном по количеству сегментов");
        }

        for (int i = 0; i < originalParts.length; i++) {
            if (originalParts[i].startsWith("{") && originalParts[i].endsWith("}")) {
                String varName = originalParts[i].substring(1, originalParts[i].length() - 1);
                variables.put(varName, requestParts[i]);
            }
        }

        return variables;
    }
}
