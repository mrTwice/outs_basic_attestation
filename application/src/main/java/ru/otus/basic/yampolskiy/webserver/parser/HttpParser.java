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
        return parserThreadLocal.get().parse(rawHttp);
    }

    private HttpRequest parse(String rawHttp) {
        HttpRequest httpRequest = new HttpRequest();
        String requestLine = getRequestLine(rawHttp);
        parseRequestLine(requestLine, httpRequest);
        httpRequest.setHeaders(getHeaders(rawHttp));
        httpRequest.setBody(getBody(rawHttp));
        logger.log(Level.DEBUG, httpRequest);
        return httpRequest;
    }

    private static String getRequestLine(String rawHttp) {
        // Разделение запроса на строки
        String[] requestLines = rawHttp.split("\r\n");

        // Первая строка запроса - стартовая строка
        return requestLines[0];
    }

    private static void parseRequestLine(String requestLines, HttpRequest httpRequest) {

        int methodEndIndex = requestLines.indexOf(' '); // Индекс конца метода запроса
        String method = requestLines.substring(0, methodEndIndex); // Метод запроса
        httpRequest.setMethod(HttpMethod.valueOf(method));

        int uriStartIndex = methodEndIndex + 1;
        int uriEndIndex = requestLines.indexOf(' ', uriStartIndex);
        String uri = requestLines.substring(uriStartIndex, uriEndIndex); // адрес запроса
        httpRequest.setUri(URI.create(uri));
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            httpRequest.setUri(URI.create(elements[0]));
            String[] keysValues = elements[1].split("&");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                httpRequest.addRequestParameter(keyValue[0], keyValue[1]);
            }
        }

        int protocolStartIndex = uriEndIndex + 1; // Индекс начала протокола и его версии
        String protocolAndVersion = requestLines.substring(protocolStartIndex); // Протокол и его версия
        httpRequest.setProtocolVersion(protocolAndVersion);
    }


    private static HttpHeaders getHeaders(String rawHttp) {
        HttpHeaders headers = new HttpHeaders();

        // Разделение запроса на строки
        String[] requestLines = rawHttp.split("\r\n");

        // Парсим первую строку запроса
        boolean isFirstLine = true;
        String firstLine = "";
        for (String line : requestLines) {
            if (isFirstLine) {
                isFirstLine = false;
                firstLine = line;
                continue;
            } else if (line.isEmpty()) {
                // Если строка пустая, это конец заголовков
                break;
            }

            // Если нет пустой строки, все строки после первой - это заголовки
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.addHeader(headerParts[0], headerParts[1]);
            }
        }

        return headers;
    }

    private static String getBody(String rawHttp) {
        // Разделение запроса на строки
        String[] requestLines = rawHttp.split("\r\n");

        // Находим индекс первой пустой строки
        int emptyLineIndex = -1;
        for (int i = 0; i < requestLines.length; i++) {
            if (requestLines[i].isEmpty()) {
                emptyLineIndex = i;
                break;
            }
        }

        // Если нет пустой строки, тела нет
        if (emptyLineIndex == -1 || emptyLineIndex == requestLines.length - 1) {
            return null;
        }

        // Сборка тела запроса
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
