package ru.otus.basic.yampolskiy.servlets;

import ru.otus.basic.yampolskiy.servlets.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServletScanner {
    private static final Logger logger = LogManager.getLogger(ServletScanner.class);
    private static final String DEFAULT_PACKAGE = "ru.otus.basic.yampolskiy.servlets.defaultservlets";
    private static final List<String> PACKAGES = new ArrayList<>();
    private static final Map<String, Route> ROUTES = new HashMap<>();

    static {
        PACKAGES.add(DEFAULT_PACKAGE);
    }

    public static Map<String, Route> scanAndRegisterServlets(String packageName) throws Exception {
        logger.debug("Starting scan and registration of servlets in package: {}", packageName);
        PACKAGES.add(packageName);
        for (String aPackage : PACKAGES) {
            scanAndRegisterServlets(aPackage, ROUTES);
        }
        logger.debug("Completed scan and registration. Registered routes: {}", ROUTES);
        return ROUTES;
    }

    private static void scanAndRegisterServlets(String packageName, Map<String, Route> routes) throws Exception {
        logger.debug("Scanning package: {}", packageName);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            logger.debug("Found resource: {}", resource);
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
                logger.debug("Registering servlet: {} with base path: {}", clazz.getName(), basePath);
                Servlet servlet = (Servlet) clazz.getDeclaredConstructor().newInstance();
                for (Method method : clazz.getDeclaredMethods()) {
                    processRouteAnnotations(routes, basePath, servlet, method);
                }
            }
        }
    }

    private static void processRouteAnnotations(Map<String, Route> routes, String basePath, Servlet servlet, Method method) {
        logger.debug("Processing method: {} in class: {}", method.getName(), method.getDeclaringClass().getName());
        if (method.isAnnotationPresent(GetRoute.class)) {
            GetRoute route = method.getAnnotation(GetRoute.class);
            String routePath = route.value().isEmpty() ? basePath : basePath + route.value();
            routes.put("GET " + routePath, new Route(servlet, method));
            logger.debug("Registered GET route: {} -> {}", routePath, method);
        }
        if (method.isAnnotationPresent(PostRoute.class)) {
            PostRoute route = method.getAnnotation(PostRoute.class);
            String routePath = route.value().isEmpty() ? basePath : basePath + route.value();
            routes.put("POST " + routePath, new Route(servlet, method));
            logger.debug("Registered POST route: {} -> {}", routePath, method);
        }
        if (method.isAnnotationPresent(PutRoute.class)) {
            PutRoute route = method.getAnnotation(PutRoute.class);
            String routePath = route.value().isEmpty() ? basePath : basePath + route.value();
            routes.put("PUT " + routePath, new Route(servlet, method));
            logger.debug("Registered PUT route: {} -> {}", routePath, method);
        }
        if (method.isAnnotationPresent(DeleteRoute.class)) {
            DeleteRoute route = method.getAnnotation(DeleteRoute.class);
            String routePath = route.value().isEmpty() ? basePath : basePath + route.value();
            routes.put("DELETE " + routePath, new Route(servlet, method));
            logger.debug("Registered DELETE route: {} -> {}", routePath, method);
        }
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        logger.debug("Searching for classes in directory: {}", directory.getAbsolutePath());
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            logger.warn("Directory does not exist: {}", directory.getAbsolutePath());
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                classes.add(Class.forName(className));
                logger.debug("Found class: {}", className);
            }
        }
        return classes;
    }
}




