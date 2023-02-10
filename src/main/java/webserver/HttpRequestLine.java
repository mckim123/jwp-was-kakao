package webserver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpMethod;
import utils.StringUtils;

public class HttpRequestLine {

    private final HttpMethod httpMethod;
    private final String requestPath;
    private final Map<String, String> queryParameters;
    private final String httpVersion;

    public HttpRequestLine(HttpMethod httpMethod, String requestPath, Map<String, String> queryParameters, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestPath = requestPath;
        this.queryParameters = queryParameters;
        this.httpVersion = httpVersion;
    }

    public HttpRequestLine(String stringStartLine) {
        if (stringStartLine == null) {
            throw new IllegalArgumentException("null point error");
        }
        String[] tokens = stringStartLine.split(" ");
        httpMethod = HttpMethod.resolve(tokens[0]);
        if (httpMethod == null) {
            throw new IllegalArgumentException("올바른 HTTP Method Type이 아닙니다.");
        }
        String requestUri = tokens[1].trim();
        requestPath = requestUri.split("\\?")[0];
        queryParameters = parseQueryParameters(requestUri);
        httpVersion = tokens[2].trim();
    }

    private static Map<String, String> parseQueryParameters(String requestTarget) {
        Map<String, String> queryParameters = new HashMap<>();
        if (requestTarget.contains("?")) {
            String parameters = requestTarget.split("\\?")[1];
            queryParameters = StringUtils.stringToMap(parameters);
        }
        return queryParameters;
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

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }
}
