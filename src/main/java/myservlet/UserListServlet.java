package myservlet;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import model.User;
import org.springframework.http.HttpStatus;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;

public class UserListServlet extends MyHttpServlet {

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws IOException {
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".html");
        Handlebars handlebars = new Handlebars(loader);

        Template template = handlebars.compile("user/list");

        Collection<User> users = DataBase.findAll();
        Map<String, Object> context = new HashMap<>();
        context.put("users", users);
        byte[] body = template.apply(context).getBytes();

        String fileExtension = "html";
        response.setStatus(HttpStatus.OK);
        response.setBody(body, fileExtension);
    }
}
