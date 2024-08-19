package ru.otus.basic.yampolskiy.webserver.http;


public abstract class Http {
    protected HttpHeaders headers;
    protected String body;

    public Http() {
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
