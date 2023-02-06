package webserver;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.http.HttpMethod;

public class HttpRequestLine {

    private final HttpMethod httpMethod;
    private final String requestPath;
    private Map<String, String> queryParameters;
    private final String httpVersion;

    public HttpRequestLine(HttpMethod httpMethod, String requestTarget, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestPath = parseRequestPath(requestTarget);
        this.queryParameters = parseQueryParameters(requestTarget);
        this.httpVersion = httpVersion;
    }

    private String parseRequestPath(String requestTarget) {
        return requestTarget.split("\\?")[0];
    }

    private Map<String, String> parseQueryParameters(String requestTarget) {
        Map<String, String> queryParameters = new HashMap<>();
        if (requestTarget.contains("\\?")) {
            String parameters = requestTarget.split("\\?")[1];
            Arrays.stream(parameters.split("&"))
                    .forEach((x) -> queryParameters.put(x.split("=")[0], x.split("=")[1]));
        };
        return queryParameters;
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
        requestPath = tokens[1].trim();
        httpVersion = tokens[2].trim();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
