package ru.otus.basic.yampolskiy.webserver;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.otus.basic.yampolskiy.webserver.interfaces.RequestHandler;
import ru.otus.basic.yampolskiy.servlets.ServletDispatcher;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private Logger logger = LogManager.getLogger(HttpServer.class);
    private int port;
    private ExecutorService threadPool;
    private RequestHandler requestHandler;


    public HttpServer(int port) throws Exception {
        this.port = port;
        this.requestHandler = new ServletDispatcher();
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.log(Level.DEBUG, "Сервер запущен на порту: {}", port);
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.submit(new ConnectionHandler(socket, requestHandler));
            }
        } catch (IOException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
    }
}
