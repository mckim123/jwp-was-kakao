package myservlet;

import java.io.IOException;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;

public class UserServlet extends MyHttpServlet {
    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws IOException {

        super.doGet(request, response);
    }
}
