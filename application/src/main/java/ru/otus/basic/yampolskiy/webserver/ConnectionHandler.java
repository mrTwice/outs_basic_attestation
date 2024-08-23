package ru.otus.basic.yampolskiy.webserver;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.webserver.parser.HttpParser;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.interfaces.RequestHandler;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler implements Runnable {
    private final Logger logger = LogManager.getLogger(ConnectionHandler.class);
    private final Socket socket;
    private final RequestHandler requestHandler;

    public ConnectionHandler(Socket socket, RequestHandler requestHandler) throws IOException {
        this.socket = socket;
        this.requestHandler = requestHandler;

    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {
            ByteArrayOutputStream requestLineAndHeaders = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            boolean headersParsed = false;
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                requestLineAndHeaders.write(buffer, 0, bytesRead);
                String currentContent = requestLineAndHeaders.toString(StandardCharsets.UTF_8);
                int headersEndIndex = currentContent.indexOf("\r\n\r\n");
                if (headersEndIndex != -1) {
                    headersParsed = true;

                    int bodyStartIndex = headersEndIndex + 4;

                    String rawRequest = currentContent.substring(0, bodyStartIndex);
                    InputStream requestStream;
                    int unreadBytes = bytesRead - bodyStartIndex;
                    if (unreadBytes > 0) {
                        requestStream = new SequenceInputStream(
                                new ByteArrayInputStream(buffer, bodyStartIndex, unreadBytes), in);
                    } else {
                        requestStream = in;
                    }
                    HttpRequest httpRequest = HttpParser.parseRawHttp(rawRequest, requestStream, socket);
                    HttpResponse httpResponse = requestHandler.execute(httpRequest);
                    if (!socket.isClosed()) {
                        logger.debug("Отправка ответа клиенту...");
                        out.write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
                        out.flush();
                        logger.debug("Ответ отправлен.");
                    } else {
                        logger.error("Сокет уже закрыт, невозможно отправить ответ.");
                    }
                    break; // Завершаем цикл после обработки
                }
            }

            if (!headersParsed) {
                logger.error("Заголовки не были распознаны.");
            }
        } catch (SocketException e) {
            logger.error("Сокет был закрыт", e);
        } catch (Exception e) {
            logger.error("Ошибка обработки запроса", e);
        } finally {
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    logger.error("Ошибка при закрытии сокета", e);
                }
            }
        }
    }
}
