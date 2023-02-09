package myservlet;

import static utils.FileIoUtils.loadFileFromClasspath;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import db.DataBase;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;

public class TemplateFileServlet extends MyHttpServlet {

    private static final Logger log = LoggerFactory.getLogger(TemplateFileServlet.class);

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws IOException {
        String requestTarget = request.getRequestPath();
        String[] splitTarget = requestTarget.split("\\.");
        String fileExtension = splitTarget[splitTarget.length - 1];
        byte[] body = new byte[0];

        try {
            body = loadFileFromClasspath("./templates" + requestTarget);
        } catch (URISyntaxException e) {
            log.debug(e.getMessage());
        }

        if ("html".equals(fileExtension)) {
            String location = requestTarget.substring(0, requestTarget.lastIndexOf("."));
            TemplateLoader loader = new ClassPathTemplateLoader();
            loader.setPrefix("/templates");
            loader.setSuffix(".html");
            Handlebars handlebars = new Handlebars(loader);
            Template template = handlebars.compile(location);
            Map<String, Object> context = new HashMap<>();

            if ("/user/list.html".equals(requestTarget) || "/user/list".equals(requestTarget)) {
                context = createUserLoginContext(request, response);
            }
            body = template.apply(context).getBytes();
        }
        response.setStatus(HttpStatus.OK);
        response.setBody(body, fileExtension);
    }

    private Map<String, Object> createUserLoginContext(MyHttpRequest request, MyHttpResponse response) {
        Map<String, Object> context = new HashMap<>();
        context.put("users", DataBase.findAll());
        return context;
    }
}
