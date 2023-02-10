package myservlet;

import db.DataBase;
import db.Session;
import java.io.IOException;
import java.util.Map;
import model.User;
import org.springframework.http.HttpStatus;
import utils.StringUtils;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;
import webserver.RequestHandler;

public class UserLoginServlet extends MyHttpServlet {

    @Override
    protected void doPost(MyHttpRequest request, MyHttpResponse response) throws IOException {
        Map<String, String> parameters = StringUtils.stringToMap(request.getBody());
        User user = DataBase.findUserById(parameters.get("userId"));
        if (user == null || !user.doesPasswordMatch(parameters.get("password"))) {
            response.setStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/user/login_failed.html");
        } else {
            Session session = findSession(request, response);
            session.setAttribute("User", user);
            response.setStatus(HttpStatus.FOUND);
            response.addHeader("Location", "/index.html");
        }
    }
}
