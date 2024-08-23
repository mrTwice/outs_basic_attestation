package ru.otus.basic.yampolskiy.webserver.http;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_DISPOSITION("Content-Disposition"),
    AUTHORIZATION("Authorization"),
    USER_AGENT("User-Agent"),
    ACCEPT("Accept"),
    COOKIE("Cookie"),
    HOST("Host"),
    REFERER("Referer"),
    CACHE_CONTROL("Cache-Control"),
    CONNECTION("Connection"),
    SERVER("Server"),
    DATE("Date"),
    LOCATION("Location");

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}
