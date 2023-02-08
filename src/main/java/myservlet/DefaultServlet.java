package myservlet;

import org.springframework.http.HttpStatus;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;

public class DefaultServlet extends MyHttpServlet {
    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) {
        byte[] body = "Hello world".getBytes();
        response.setStatus(HttpStatus.OK);
        response.setBody(body, "html");
    }
}
