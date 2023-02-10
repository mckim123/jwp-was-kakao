package myservlet;

import db.DataBase;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
        DataBase.addUser(createUserByQueryParameters(queryParameters));
        response.setStatus(HttpStatus.CREATED);
    }

    @Override
    protected void doPost(MyHttpRequest request, MyHttpResponse response) {
        Map<String, String> queryParameters = StringUtils.stringToMap(request.getBody());
        DataBase.addUser(createUserByQueryParameters(queryParameters));
        response.setStatus(HttpStatus.FOUND);
        response.addHeader("Location", "/index.html");
    }

    private User createUserByQueryParameters(Map<String, String> queryParameters) {
        try {
            return new User(
                    decode(queryParameters.get("userId")),
                    decode(queryParameters.get("password")),
                    decode(queryParameters.get("name")),
                    decode(queryParameters.get("email"))
            );
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String decode(String str) throws UnsupportedEncodingException {
        return URLDecoder.decode(str,"UTF-8");
    }
}
