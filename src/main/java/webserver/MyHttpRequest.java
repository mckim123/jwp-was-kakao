package webserver;

import java.util.Map;
import org.springframework.http.HttpMethod;

public class MyHttpRequest {
    private final HttpRequestLine httpRequestLine;
    private final Map<String, String> headers;
    private final String body;

    public MyHttpRequest(HttpRequestLine httpRequestLine, Map<String, String> headers) {
        this(httpRequestLine, headers, null);
    }

    public MyHttpRequest(HttpRequestLine httpRequestLine, Map<String, String> headers, String body) {
        this.httpRequestLine = httpRequestLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpRequestLine getHttpRequestLine() {
        return httpRequestLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParameters() {
        return httpRequestLine.getQueryParameters();
    }

    public String getBody() {
        return body;
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    public String getRequestPath() {
        return httpRequestLine.getRequestPath();
    }

    public String getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }
}
