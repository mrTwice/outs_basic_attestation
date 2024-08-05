package ru.otus.basic.yampolskiy;

import ru.otus.basic.yampolskiy.webserver.ConfigLoader;
import ru.otus.basic.yampolskiy.webserver.HttpServer;

public class Application {
    public static void main(String[] args) throws Exception {
        String portProperty = System.getProperty("server.port");
        int port;
        if (portProperty != null) {
            port = Integer.parseInt(portProperty);
        } else {
            port = ConfigLoader.getIntProperty("server.port", 8189);
        }
        new HttpServer(port).start();
    }
}