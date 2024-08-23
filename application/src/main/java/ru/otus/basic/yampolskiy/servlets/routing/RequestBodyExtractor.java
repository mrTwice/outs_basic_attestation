package ru.otus.basic.yampolskiy.servlets.routing;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.servlets.annotations.RequestBody;
import ru.otus.basic.yampolskiy.servlets.annotations.UploadedFile;
import ru.otus.basic.yampolskiy.servlets.models.HttpServletRequest;
import ru.otus.basic.yampolskiy.servlets.utils.ObjectMapperSingleton;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RequestBodyExtractor {
    private static final Logger logger = LogManager.getLogger(RequestBodyExtractor.class);
    private static final ObjectMapper objectMapper = ObjectMapperSingleton.getInstance();

    public static Object extractRequestBody(HttpServletRequest request, Class<?> targetType) throws IOException {
        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            JsonParser parser = objectMapper.getFactory().createParser(request.getBodyInputStream());
            return objectMapper.readValue(parser, targetType);
        } else if (contentType != null && contentType.contains("multipart/form-data")) {
            if (targetType.equals(File.class)) {
                return handleMultipartFile(request);
            } else {
                throw new UnsupportedOperationException("Unsupported type for multipart data: " + targetType.getName());
            }
        }
        throw new UnsupportedOperationException("Unsupported content type: " + contentType);
    }

    public static File handleMultipartFile(HttpServletRequest request) throws IOException {
        String contentDisposition = request.getHeader(HttpHeader.CONTENT_DISPOSITION);
        String fileName = extractFileName(contentDisposition);
        File file = new File("./uploads/" + fileName);  // Убедитесь, что директория `uploads` существует

        try (InputStream inputStream = request.getBodyInputStream();
             FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return file;
    }

    private static String extractFileName(String contentDisposition) {
        if (contentDisposition == null) {
            throw new IllegalArgumentException("Content-Disposition header is missing");
        }

        String[] tokens = contentDisposition.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }
}


