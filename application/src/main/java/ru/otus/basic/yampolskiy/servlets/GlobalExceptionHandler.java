package ru.otus.basic.yampolskiy.servlets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.exceptions.BadRequestException;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class GlobalExceptionHandler {
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

    public static HttpServletResponse handleException(Throwable ex, HttpServletRequest request) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);

        if (ex instanceof BadRequestException) {
            return createErrorResponse(request, HttpStatus.BAD_REQUEST, ex.getMessage());
        } else {
            return createErrorResponse(request, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    private static HttpServletResponse createErrorResponse(HttpServletRequest request, HttpStatus status, String message) {
        String responseBody = "";
        try {
            responseBody = objectMapper.writeValueAsString(Error.createError(status.getMessage(), status.getCode(), message));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing error response: {}", e.getMessage());
        }

        return new HttpServletResponse.Builder()
                .setProtocolVersion(request.getProtocolVersion())
                .setStatus(status)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "YourServer/1.0")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/json")
                .addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length))
                .setBody(responseBody)
                .build();
    }
}
