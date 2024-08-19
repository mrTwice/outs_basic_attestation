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
import java.util.concurrent.TimeUnit;

public class HttpServer {
    private final Logger logger = LogManager.getLogger(HttpServer.class);
    private final int port;
    private final ExecutorService threadPool;
    private final RequestHandler requestHandler;
    private volatile boolean isRunning;

    public HttpServer(int port) throws Exception {
        this.port = port;
        this.requestHandler = createRequestHandler();
        this.threadPool = createThreadPool();
    }

    private ExecutorService createThreadPool() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    private RequestHandler createRequestHandler() throws Exception {
        return new ServletDispatcher();
    }

    public void start() {
        isRunning = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.debug("Сервер запущен на порту: {}", port);
            while (isRunning) {
                try {
                    Socket socket = serverSocket.accept();
                    threadPool.submit(new ConnectionHandler(socket, requestHandler));
                } catch (IOException e) {
                    if (isRunning) {
                        logger.error("Ошибка при принятии подключения", e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка запуска сервера", e);
        } finally {
            stop();
        }
    }

    public void stop() {
        isRunning = false;
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    logger.error("Пул потоков не остановлен");
                }
            }
        } catch (InterruptedException ie) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        logger.debug("Сервер остановлен");
    }
}
