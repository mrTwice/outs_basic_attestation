package ru.otus.basic.yampolskiy.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;

import java.io.IOException;

public class RequestBodyExtractor {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static Object extractRequestBody(HttpServletRequest request, Class<?> targetType) throws JsonProcessingException {
        return objectMapper.readValue(request.getBody(), targetType);
    }
}