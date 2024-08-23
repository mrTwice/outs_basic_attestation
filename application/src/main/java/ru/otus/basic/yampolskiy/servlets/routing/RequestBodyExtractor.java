package ru.otus.basic.yampolskiy.servlets.routing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.domain.entities.MultipartData;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;

import java.io.IOException;
import java.io.InputStream;

public class RequestBodyExtractor {
    private static final Logger logger = LogManager.getLogger(RequestBodyExtractor.class);
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static Object extractRequestBody(HttpServletRequest request, Class<?> targetType) throws IOException {
        String contentType = request.getContentType();

        if (contentType == null) {
            throw new UnsupportedOperationException("Content type is missing in the request");
        }

        if (contentType.contains("application/json")) {
            return extractJsonBody(request, targetType);
        } else if (contentType.contains("multipart/form-data")) {
            if (targetType.equals(MultipartData.class)) {
                return extractMultipartData(request);
            } else {
                throw new UnsupportedOperationException("Unsupported type for multipart data: " + targetType.getName());
            }
        } else {
            throw new UnsupportedOperationException("Unsupported content type: " + contentType);
        }
    }

    private static Object extractJsonBody(HttpServletRequest request, Class<?> targetType) throws IOException {
        JsonParser parser = objectMapper.getFactory().createParser(request.getBodyInputStream());
        return objectMapper.readValue(parser, targetType);
    }

    static MultipartData extractMultipartData(HttpServletRequest request) throws IOException {
        String contentLength = request.getHeader(HttpHeader.CONTENT_LENGTH);
        String boundaryPrefix = "boundary=";
        int boundaryIndex = request.getContentType().indexOf(boundaryPrefix);
        if (boundaryIndex == -1) {
            throw new IllegalArgumentException("Boundary не найден в Content-Type");
        }
        String boundary = request.getContentType().substring(boundaryIndex + boundaryPrefix.length()).trim();
        return new MultipartData(request.getBodyInputStream(), contentLength, boundary);
    }

}



