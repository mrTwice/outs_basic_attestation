package ru.otus.basic.yampolskiy.servlets.routing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;

public class RequestBodyExtractor {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static Object extractRequestBody(HttpServletRequest request, Class<?> targetType) throws JsonProcessingException {
        return objectMapper.readValue(request.getBody(), targetType);
    }
}