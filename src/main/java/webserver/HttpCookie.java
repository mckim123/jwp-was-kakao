package webserver;

import java.util.Map;
import java.util.TreeMap;
import utils.StringUtils;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private Map<String, String> cookie;

    public HttpCookie(String jSessionId) {
        this(jSessionId, "/");
    }

    public HttpCookie(String jSessionId, String path) {
        cookie = new TreeMap<>();
        cookie.put("JSESSIONID", jSessionId);
        cookie.put("Path", path);
    }

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie parseCookie(String cookieString) {
        if (StringUtils.isEmpty(cookieString)) {
            return null;
        }
        Map<String, String> cookie = StringUtils.stringToMapWithTrim(cookieString, ";", "=");
        return new HttpCookie(cookie);
    }

    public boolean validatePath(String requestPath) {
        String path = cookie.getOrDefault("Path", "/");
        return requestPath.startsWith(path);
    }

    public String getAttribute(String key) {
        return cookie.get(key);
    }

    public String getJSessionId() {
        return cookie.get(JSESSIONID);
    }

    @Override
    public String toString() {
        if (cookie.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        cookie.forEach((key, value) -> {
            sb.append(key);
            sb.append("=");
            sb.append(value);
            sb.append("; ");
        });
        return sb.substring(0, sb.lastIndexOf(";"));
    }
}

