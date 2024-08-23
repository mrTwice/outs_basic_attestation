package ru.otus.basic.yampolskiy.servlets.models;

import ru.otus.basic.yampolskiy.webserver.http.HttpHeader;
import ru.otus.basic.yampolskiy.webserver.http.HttpMethod;
import ru.otus.basic.yampolskiy.webserver.http.HttpRequest;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;


public class HttpServletRequest {
    private final HttpRequest httpRequest;
    private final RequestContext requestContext;

    public HttpServletRequest(HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
        requestContext = new RequestContext();
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public String getUri() {
        return httpRequest.getUri().getPath();
    }

//    public String getBody() {
//        return httpRequest.getBody();
//    }

    public String getProtocolVersion() {
        return httpRequest.getProtocolVersion();
    }

    public String getRoutingKey() {
        return httpRequest.getRoutingKey();
    }

    public String getHeader(HttpHeader httpHeader) {
        return httpRequest.getHeader(httpHeader);
    }

    public boolean containsParameter(String key) {
        return httpRequest.containsParameter(key);
    }

    public String getParameter(String key) {
        return httpRequest.getParameter(key);
    }

    public HttpMethod getMethod() {
        return httpRequest.getMethod();
    }

    public Map<String, String> getRequestParameters(){
        return httpRequest.getRequestParameters();
    }

    public String getContentType(){
        return httpRequest.getHeader(HttpHeader.CONTENT_TYPE);
    }

    public InputStream getBodyInputStream () {
        return httpRequest.getBodyStream();
    }
}
