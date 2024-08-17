package ru.otus.basic.yampolskiy.servlets.models;

import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpResponse;
import ru.otus.basic.yampolskiy.webserver.http.HttpStatus;

public class HttpServletResponse {
    private HttpResponse httpResponse;

    private HttpServletResponse(Builder builder) {
        this.httpResponse = builder.builder.build();
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public void addHeader(HttpHeader header, String value) {
        httpResponse.addHeader(header, value);
    }

    @Override
    public String toString() {
        return httpResponse.toString();
    }

    public static class Builder {
        private HttpResponse.Builder builder;

        public Builder() {
            this.builder = new HttpResponse.Builder();
        }

        public Builder setProtocolVersion(String protocolVersion) {
            builder.setProtocolVersion(protocolVersion);
            return this;
        }

        public Builder setStatus(HttpStatus status) {
            builder.setStatus(status);
            return this;
        }

        public Builder addHeader(HttpHeader header, String value) {
            builder.addHeader(header, value);
            return this;
        }

        public Builder setContentType(String contentType) {
            builder.addHeader(HttpHeader.CONTENT_TYPE, contentType);
            return this;
        }

        public Builder setBody(String body) {
            builder.setBody(body);
            return this;
        }

        public HttpServletResponse build() {
            return new HttpServletResponse(this);
        }
    }
}

