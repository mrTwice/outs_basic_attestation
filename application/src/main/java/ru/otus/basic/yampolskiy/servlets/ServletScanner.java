package ru.otus.basic.yampolskiy.servlets;

import ru.otus.basic.yampolskiy.servlets.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

public class ServletScanner {

    public static Map<String, Method> scanAndRegisterServlets(String packageName) throws Exception {
        Map<String, Method> routes = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(WebServlet.class)) {
                WebServlet webServlet = clazz.getAnnotation(WebServlet.class);
                String basePath = webServlet.value();
                for (Method method : clazz.getDeclaredMethods()) {
                    processRouteAnnotations(routes, basePath, method);
                }
            }
        }
        return routes;
    }

    private static void processRouteAnnotations(Map<String, Method> routes, String basePath, Method method) {
        if (method.isAnnotationPresent(GetRoute.class)) {
            GetRoute route = method.getAnnotation(GetRoute.class);
            String routePath = route.value().isEmpty() ? basePath : basePath + route.value();
            routes.put("GET " + routePath, method);
        }
        if (method.isAnnotationPresent(PostRoute.class)) {
            PostRoute route = method.getAnnotation(PostRoute.class);
            String routePath = route.value().isEmpty() ? basePath : basePath + route.value();
            routes.put("POST " + routePath, method);
        }
        if (method.isAnnotationPresent(PutRoute.class)) {
            PutRoute route = method.getAnnotation(PutRoute.class);
            String routePath = route.value().isEmpty() ? basePath : basePath + route.value();
            routes.put("PUT " + routePath, method);
        }
        if (method.isAnnotationPresent(DeleteRoute.class)) {
            DeleteRoute route = method.getAnnotation(DeleteRoute.class);
            String routePath = route.value().isEmpty() ? basePath : basePath + route.value();
            routes.put("DELETE " + routePath, method);
        }
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
