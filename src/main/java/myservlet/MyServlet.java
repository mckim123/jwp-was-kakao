package myservlet;

import java.io.IOException;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;

public interface MyServlet {

    void init();

    void service(MyHttpRequest request, MyHttpResponse response) throws IOException;

    void destroy();
}
