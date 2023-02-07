package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class MyHttpResponse {

    private static final Logger logger = LoggerFactory.getLogger(MyHttpResponse.class);

    private final HttpStatus status;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private final byte[] body;

    public MyHttpResponse(HttpStatus status) {
        this(status, new byte[0]);
    }

    public MyHttpResponse(HttpStatus status, byte[] body) {
        this.status = status;
        this.body = body;
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

    private String getResponseLine() {
        return "HTTP/1.1 " + status.value() + " " + status.name() + " \r\n";
    }
}
