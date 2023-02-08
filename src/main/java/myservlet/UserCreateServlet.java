package myservlet;

import db.DataBase;
import java.util.Map;
import model.User;
import org.springframework.http.HttpStatus;
import utils.StringUtils;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;

public class UserCreateServlet extends MyHttpServlet {

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) {
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
    protected void doPost(MyHttpRequest request, MyHttpResponse response) {
        Map<String, String> queryParameters = StringUtils.stringToMap(request.getBody());
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
