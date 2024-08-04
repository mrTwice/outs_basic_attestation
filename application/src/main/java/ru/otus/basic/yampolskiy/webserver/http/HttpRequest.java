package ru.otus.basic.yampolskiy.webserver.http;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest extends Http{
    private HttpMethod method;
    private URI uri;
    private String protocolVersion;
    private Map<String, String> requestParameters;

    public HttpRequest() {
        this.requestParameters = new HashMap<>();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public void addRequestParameter(String key, String value) {
        requestParameters.put(key, value);
    }

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public boolean containsParameter(String key) {
        return requestParameters.containsKey(key);
    }

    public String getParameter(String key) {
        return requestParameters.get(key);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nHttpRequest {\n");
        sb.append("  method=").append(method != null ? method : "null").append(",\n");
        sb.append("  uri=").append(uri != null ? uri : "null").append(",\n");
        sb.append("  protocolVersion='").append(protocolVersion != null ? protocolVersion : "null").append("',\n");
        sb.append("  requestParameters=").append(requestParameters != null ? requestParameters : "null").append(",\n");
        sb.append("  headers=").append(headers != null ? headers : "null").append(",\n");
        sb.append("  body='").append(body != null ? body : "null").append("'\n");
        sb.append("}");
        return sb.toString();
    }
}
