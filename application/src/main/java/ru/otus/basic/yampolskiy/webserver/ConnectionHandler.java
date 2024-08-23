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
            byte[] buffer = new byte[1];
            int index = 0;
            byte[] lastFourBytes = new byte[4];
            boolean headersParsed = false;

            // Чтение байт по одному
            while (in.read(buffer) != -1) {
                requestLineAndHeaders.write(buffer[0]);

                // Обновляем последние четыре байта
                lastFourBytes[index % 4] = buffer[0];
                index++;

                // Проверяем на "\r\n\r\n"
                if (index >= 4 &&
                        lastFourBytes[(index - 4) % 4] == '\r' &&
                        lastFourBytes[(index - 3) % 4] == '\n' &&
                        lastFourBytes[(index - 2) % 4] == '\r' &&
                        lastFourBytes[(index - 1) % 4] == '\n') {

                    headersParsed = true;
                    break; // Завершаем чтение заголовков
                }
            }

            if (headersParsed) {
                String rawRequest = requestLineAndHeaders.toString(StandardCharsets.UTF_8);

                // Считаем оставшуюся часть потока как тело запроса
                InputStream requestStream = in;

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
            } else {
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
