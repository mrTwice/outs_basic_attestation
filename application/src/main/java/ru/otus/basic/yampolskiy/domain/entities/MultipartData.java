package ru.otus.basic.yampolskiy.domain.entities;

import java.io.IOException;
import java.io.InputStream;

public class MultipartData {
    private final String contentLength;
    private final String boundary;
    private final InputStream inputStream;

    public MultipartData(InputStream inputStream, String contentLength, String boundary) throws IOException {
        this.contentLength= contentLength;
        this.boundary = boundary;
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getContentLength() {
        return contentLength;
    }

    public String getBoundary() {
        return boundary;
    }
}

