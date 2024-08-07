package ru.otus.basic.yampolskiy.webserver.parser;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.webserver.http.HttpHeaders;
import ru.otus.basic.yampolskiy.webserver.http.HttpMethod;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;

import java.net.URI;

public class HttpParser {
    private static Logger logger = LogManager.getLogger(HttpParser.class);

    private static ThreadLocal<HttpParser> parserThreadLocal = ThreadLocal.withInitial(HttpParser::new);

    public static HttpRequest parseRawHttp(String rawHttp) {
        logger.log(Level.DEBUG, "Parsing raw HTTP request: \n{}", rawHttp);
        return parserThreadLocal.get().parse(rawHttp);
    }

    private HttpRequest parse(String rawHttp) {
        HttpRequest httpRequest = new HttpRequest();
        try {
            String requestLine = getRequestLine(rawHttp);
            logger.log(Level.DEBUG, "Request Line: {}", requestLine);
            parseRequestLine(requestLine, httpRequest);
            httpRequest.setHeaders(getHeaders(rawHttp));
            logger.log(Level.DEBUG, "Headers: {}", httpRequest.getHeaders());
            httpRequest.setBody(getBody(rawHttp));
            logger.log(Level.DEBUG, "Body: {}", httpRequest.getBody());
        } catch (Exception e) {
            logger.log(Level.ERROR, "Error parsing HTTP request: ", e);
        }
        logger.log(Level.DEBUG, "Parsed HttpRequest: {}", httpRequest);
        return httpRequest;
    }

    private static String getRequestLine(String rawHttp) {
        String[] requestLines = rawHttp.split("\r\n");
        return requestLines[0];
    }

    private static void parseRequestLine(String requestLine, HttpRequest httpRequest) {
        int methodEndIndex = requestLine.indexOf(' ');
        String method = requestLine.substring(0, methodEndIndex);
        httpRequest.setMethod(HttpMethod.valueOf(method));
        logger.log(Level.DEBUG, "HTTP Method: {}", method);

        int uriStartIndex = methodEndIndex + 1;
        int uriEndIndex = requestLine.indexOf(' ', uriStartIndex);
        String uri = requestLine.substring(uriStartIndex, uriEndIndex);
        httpRequest.setUri(URI.create(uri));
        logger.log(Level.DEBUG, "URI: {}", uri);
        if (uri.contains("?")) {
            String[] elements = uri.split("\\?");
            httpRequest.setUri(URI.create(elements[0]));
            String[] keysValues = elements[1].split("&");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                httpRequest.addRequestParameter(keyValue[0], keyValue[1]);
                logger.log(Level.DEBUG, "Parameter: {} = {}", keyValue[0], keyValue[1]);
            }
        }

        int protocolStartIndex = uriEndIndex + 1;
        String protocolAndVersion = requestLine.substring(protocolStartIndex);
        httpRequest.setProtocolVersion(protocolAndVersion);
        logger.log(Level.DEBUG, "Protocol and Version: {}", protocolAndVersion);
    }

    private static HttpHeaders getHeaders(String rawHttp) {
        HttpHeaders headers = new HttpHeaders();
        String[] requestLines = rawHttp.split("\r\n");
        boolean isFirstLine = true;
        for (String line : requestLines) {
            if (isFirstLine) {
                isFirstLine = false;
                continue;
            } else if (line.isEmpty()) {
                break;
            }
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.addHeader(headerParts[0], headerParts[1]);
                logger.log(Level.DEBUG, "Header: {} = {}", headerParts[0], headerParts[1]);
            }
        }
        return headers;
    }

    private static String getBody(String rawHttp) {
        String[] requestLines = rawHttp.split("\r\n");
        int emptyLineIndex = -1;
        for (int i = 0; i < requestLines.length; i++) {
            if (requestLines[i].isEmpty()) {
                emptyLineIndex = i;
                break;
            }
        }
        if (emptyLineIndex == -1 || emptyLineIndex == requestLines.length - 1) {
            return null;
        }
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = emptyLineIndex + 1; i < requestLines.length; i++) {
            bodyBuilder.append(requestLines[i]);
            if (i < requestLines.length - 1) {
                bodyBuilder.append("\r\n");
            }
        }
        return bodyBuilder.toString();
    }
}
