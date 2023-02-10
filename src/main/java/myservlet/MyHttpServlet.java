package myservlet;

import db.Session;
import db.SessionManager;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;
import webserver.RequestHandler;

public abstract class MyHttpServlet implements MyServlet {

    @Override
    public void init() {
        // NOOP by default
    }

    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws IOException
    {
        String msg = "http.method_get_not_supported";
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, msg);
    }

    protected void doHead(MyHttpRequest request, MyHttpResponse response) throws IOException {
        String msg = "http.method_head_not_supported";
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, msg);
    }

    protected void doPost(MyHttpRequest request, MyHttpResponse response) throws IOException {
        String msg = "http.method_post_not_supported";
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, msg);
    }

    protected void doPut(MyHttpRequest request, MyHttpResponse response) throws IOException {
        String msg = "http.method_put_not_supported";
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, msg);
    }

    protected void doDelete(MyHttpRequest request, MyHttpResponse response) throws IOException {
        String msg = "http.method_delete_not_supported";
        response.sendError(HttpStatus.METHOD_NOT_ALLOWED, msg);
    }

    @Override
    public void service(MyHttpRequest request, MyHttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();
        if (method.equals(HttpMethod.GET)) {
            doGet(request, response);
        } else if (method.equals(HttpMethod.HEAD)) {
            doHead(request, response);
        } else if (method.equals(HttpMethod.POST)) {
            doPost(request, response);
        } else if (method.equals(HttpMethod.PUT)) {
            doPut(request, response);
        } else if (method.equals(HttpMethod.DELETE)) {
            doDelete(request, response);
        } else {
            String errMsg = "http.method_not_implemented :" + method;
            response.sendError(HttpStatus.NOT_IMPLEMENTED, errMsg);
        }
    }

    @Override
    public void destroy() {
        // NOOP by default
    }

    protected Session findSession(MyHttpRequest request, MyHttpResponse response) {
        return SessionManager.getInstance().findSession(findJSessionId(request, response));
    }

    protected String findJSessionId(MyHttpRequest request, MyHttpResponse response) {
        try {
            return response.getJSessionId();
        } catch (NullPointerException e) {
            return request.getJSessionId();
        }
    }
}
