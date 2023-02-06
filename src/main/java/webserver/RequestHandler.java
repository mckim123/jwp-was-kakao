package webserver;

import static utils.FileIoUtils.loadFileFromRequestTarget;
import static utils.IOUtils.parseHttpRequest;

import db.DataBase;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static final Map<String, String> mimeType = new HashMap<>(){{
        put("html", "text/html");
        put("css", "text/css");
        put("js", "text/javascript");
        put("ico" ,"image/vnd.microsoft.icon");
        put("png" ,"image/png");
        put("woff", "application/x-font-woff");
        put("woff2", "application/x-font-woff2");
    }};

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            MyHttpRequest httpRequest = parseHttpRequest(bufferedReader);

            DataOutputStream dos = new DataOutputStream(out);

            MyHttpResponse response = execute(httpRequest);
            response.writeResponse(dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    private MyHttpResponse execute(MyHttpRequest request) throws IOException, URISyntaxException{
//        byte[] body = "Hello world".getBytes();
//        String typeOfBodyContent = "text/html";
        String requestTarget = request.getRequestPath();

        if (isFileRequestTarget(requestTarget)) {
            byte[] body = loadFileFromRequestTarget(requestTarget);
            String[] splitTarget = requestTarget.split("\\.");
            String typeOfBodyContent = mimeType.get(splitTarget[splitTarget.length - 1]);
            MyHttpResponse response = new MyHttpResponse(HttpStatus.OK, body);
            response.setContentType(typeOfBodyContent);
            response.setContentLength(body.length);
            return response;
        }

        if (HttpMethod.GET.equals(request.getHttpMethod()) && "/user/create".equals(request.getRequestPath())) {
            Map<String, String> queryParameters = request.getQueryParameters();
            DataBase.addUser(
                    new User(queryParameters.get("userId"),
                            queryParameters.get("password"),
                            queryParameters.get("name"),
                            queryParameters.get("email"))
            );
            return new MyHttpResponse(HttpStatus.CREATED);
        }
        if (HttpMethod.GET.equals(request.getHttpMethod()) && "/".equals(request.getRequestPath())) {
            byte[] body = "Hello world".getBytes();
            MyHttpResponse response = new MyHttpResponse(HttpStatus.OK, body);
            response.setContentType("text/html");
            response.setContentLength(body.length);
            return response;
        }

        if (HttpMethod.POST.equals(request.getHttpMethod()) && "/user/create".equals(request.getRequestPath())) {
            String body = request.getBody();
            Map<String, String> queryParameters = new HashMap<>();

            Arrays.stream(body.split("&")).forEach((x) -> queryParameters.put(x.split("=")[0], x.split("=")[1]));

            DataBase.addUser(
                    new User(queryParameters.get("userId"),
                            queryParameters.get("password"),
                            queryParameters.get("name"),
                            queryParameters.get("email"))
            );
            MyHttpResponse response = new MyHttpResponse(HttpStatus.FOUND);
            response.addHeader("Location", "/index.html");
            return response;
        }

        throw new IllegalArgumentException();
    }

    private static boolean isFileRequestTarget(String requestTarget) {
        return requestTarget.contains(".");
    }

    private static boolean isNullOrEmpty(String line) {
        return line == null || "".equals(line);
    }

}
