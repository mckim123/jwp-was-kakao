package myservlet;

import static utils.FileIoUtils.loadFileFromRequestTarget;

import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.http.HttpStatus;
import webserver.MyHttpRequest;
import webserver.MyHttpResponse;

public class TemplateServlet extends MyHttpServlet {

    @Override
    protected void doGet(MyHttpRequest request, MyHttpResponse response) throws IOException {
        String requestTarget = request.getRequestPath();
        try {
            byte[] body = loadFileFromRequestTarget(requestTarget);
            String[] splitTarget = requestTarget.split("\\.");
            String fileExtension = splitTarget[splitTarget.length - 1];
            response.setStatus(HttpStatus.OK);
            response.setBody(body, fileExtension);
        } catch (URISyntaxException e) {
            response.sendError(HttpStatus.NOT_FOUND, "File not Found");
        }
    }
}
