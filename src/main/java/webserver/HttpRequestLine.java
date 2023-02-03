package webserver;


import org.springframework.http.HttpMethod;

public class HttpRequestLine {

    private final HttpMethod httpMethod;
    private final String requestTarget;
    private final String httpVersion;

    public HttpRequestLine(HttpMethod httpMethod, String requestTarget, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }

    public HttpRequestLine(String stringStartLine) {
        if (stringStartLine == null) {
            throw new RuntimeException("null point error");
        }
        String[] tokens = stringStartLine.split(" ");
        httpMethod = HttpMethod.resolve(tokens[0]);
        if (httpMethod == null) {
            throw new RuntimeException("올바른 HTTP Method Type이 아닙니다."); // Todo
        }
        requestTarget = tokens[1].trim();
        httpVersion = tokens[2].trim();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
