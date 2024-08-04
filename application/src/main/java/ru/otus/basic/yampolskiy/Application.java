package ru.otus.basic.yampolskiy;

import ru.otus.basic.yampolskiy.webserver.HttpServer;

public class Application {
    public static void main(String[] args) throws Exception {
        new HttpServer(8189).start();
    }
}