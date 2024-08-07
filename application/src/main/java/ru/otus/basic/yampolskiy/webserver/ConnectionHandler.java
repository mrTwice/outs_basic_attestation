package ru.otus.basic.yampolskiy.webserver;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.webserver.parser.HttpParser;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.interfaces.RequestHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ConnectionHandler implements Runnable {
    private Logger logger = LogManager.getLogger(ConnectionHandler.class);
    private Socket socket;
    private RequestHandler requestHandler;

    public ConnectionHandler(Socket socket, RequestHandler requestHandler) throws IOException {
        this.socket = socket;
        this.requestHandler = requestHandler;

    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int n = in.read(buffer);
            if (n > 0){
                String rawRequest = new String(buffer, 0, n);
                HttpRequest httpRequest = HttpParser.parseRawHttp(rawRequest);
                HttpResponse httpResponse = requestHandler.execute(httpRequest);
                out.write(httpResponse.toString().getBytes(StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            logger.log(Level.ERROR, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.ERROR, e);
            }
        }
    }
}
