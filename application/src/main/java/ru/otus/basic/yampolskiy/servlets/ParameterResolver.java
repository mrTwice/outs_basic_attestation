package ru.otus.basic.yampolskiy.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.annotations.PathVariable;
import ru.otus.basic.yampolskiy.servlets.annotations.RequestBody;
import ru.otus.basic.yampolskiy.servlets.annotations.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ParameterResolver {
    private static final Logger logger = LogManager.getLogger(ParameterResolver.class);

    static public Object[] resolveParameters(Method method, HttpServletRequest request) throws JsonProcessingException {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] params = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (annotation instanceof PathVariable) {
                    String pathVariableName = ((PathVariable) annotation).value();
                    String pathVariableValue = request.getRequestContext().getAttribute(pathVariableName);

                    logger.debug("Извлеченная переменная пути перед конвертацией: " + pathVariableName + " = " + pathVariableValue);
                    params[i] = convertParameter(pathVariableValue, parameterTypes[i]);
                    logger.debug("Извлеченная переменная пути после конвертации: " + pathVariableName + " = " + params[i]);
                } else if (annotation instanceof RequestParam) {
                    String paramName = ((RequestParam) annotation).value();
                    String paramValue = request.getParameter(paramName);

                    logger.debug("Извлеченный параметр запроса перед конвертацией: " + paramName + " = " + paramValue);
                    params[i] = convertParameter(paramValue, parameterTypes[i]);
                    logger.debug("Извлеченный параметр запроса после конвертации: " + paramName + " = " + params[i]);
                } else if (annotation instanceof RequestBody) {
                    logger.debug("Десериализация тела запроса в тип: " + parameterTypes[i].getName());
                    params[i] = RequestBodyExtractor.extractRequestBody(request, parameterTypes[i]);
                    logger.debug("Десериализованное тело запроса: " + params[i]);
                }
            }

            if (params[i] == null && !parameterTypes[i].isPrimitive()) {
                params[i] = null;
            }
        }

        return params;
    }

    static private Object convertParameter(String paramValue, Class<?> targetType) {
        if (paramValue == null) {
            return null;
        }

        try {
            if (targetType == Integer.class || targetType == int.class) {
                return Integer.parseInt(paramValue);
            } else if (targetType == Long.class || targetType == long.class) {
                return Long.parseLong(paramValue);
            } else if (targetType == Double.class || targetType == double.class) {
                return Double.parseDouble(paramValue);
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return Boolean.parseBoolean(paramValue);
            }
        } catch (NumberFormatException e) {
            logger.error("Ошибка конвертации параметра: " + paramValue + " в тип " + targetType, e);
            return null;
        }

        return paramValue;
    }
}
