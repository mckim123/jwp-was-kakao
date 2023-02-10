package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class MyHttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(MyHttpResponse.class);
    public static final Map<String, String> MIME_TYPE = new HashMap<>();

    static {
        MIME_TYPE.put("html", "text/html");
        MIME_TYPE.put("css", "text/css");
        MIME_TYPE.put("js", "text/javascript");
        MIME_TYPE.put("ico" ,"image/vnd.microsoft.icon");
        MIME_TYPE.put("png" ,"image/png");
        MIME_TYPE.put("woff", "application/x-font-woff");
        MIME_TYPE.put("woff2", "application/x-font-woff2");
    }

    private HttpStatus status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public MyHttpResponse() {
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setStatus(int status) {
        this.status = HttpStatus.resolve(status);
    }

    public void setBody(byte[] body, String extension) {
        this.body = body;
        setContentType(MIME_TYPE.get(extension));
        setContentLength(body.length);
    }

    public void setContentType(String typeOfBodyContent) {
        headers.put("Content-Type", typeOfBodyContent + ";charset=utf-8");
    }

    public void setContentLength(int lengthOfBodyContent) {
        headers.put("Content-Length", String.valueOf(lengthOfBodyContent));
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void writeResponse(DataOutputStream dos) throws IOException{
        try {
            dos.writeBytes(getResponseLine());
            headers.forEach((key, value) -> writeLine(dos, key + ": " + value + " \r\n"));
            dos.writeBytes("\r\n");

            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
    
    private void writeLine(DataOutputStream dos, String line) {
        try {
            dos.writeBytes(line);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean isStatusAssigned() {
        return status != null;
    }

    private String getResponseLine() {
        return "HTTP/1.1 " + status.value() + " " + status.name() + " \r\n";
    }

    public void sendError(HttpStatus status, String msg) {
        setStatus(status);
        addHeader("message", msg);
    }

    public String getJSessionId() {
        return HttpCookie.parseCookie(headers.get("Set-Cookie")).getJSessionId();
    }
}
