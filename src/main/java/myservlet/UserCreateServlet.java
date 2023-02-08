package myservlet;

import db.DataBase;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import model.User;
import org.springframework.http.HttpStatus;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;

public class UserCreateServlet extends MyHttpServlet {

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws IOException {
        Map<String, String> queryParameters = request.getQueryParameters();
        DataBase.addUser(
                new User(queryParameters.get("userId"),
                        queryParameters.get("password"),
                        queryParameters.get("name"),
                        queryParameters.get("email"))
        );
        response.setStatus(HttpStatus.CREATED);
    }

    @Override
    protected void doPost(MyHttpRequest request, MyHttpResponse response) throws IOException {
        String body = request.getBody();
        Map<String, String> queryParameters = new HashMap<>();
        Arrays.stream(body.split("&"))
                .forEach(x -> queryParameters.put(x.split("=")[0], x.split("=")[1]));
        DataBase.addUser(
                new User(queryParameters.get("userId"),
                        queryParameters.get("password"),
                        queryParameters.get("name"),
                        queryParameters.get("email"))
        );
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }
}
