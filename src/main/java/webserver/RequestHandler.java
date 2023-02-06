package webserver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import utils.FileIoUtils;
import utils.IOUtils;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

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
            MyHttpRequest httpRequest = IOUtils.parseHttpRequest(bufferedReader);

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Hello world".getBytes();
            String typeOfBodyContent = "text/html";
            String requestTarget = httpRequest.getRequestTarget();

            if (requestTarget.contains(".")) {
                try {
                    body = FileIoUtils.loadFileFromClasspath("./templates" + requestTarget);
                } catch (NullPointerException e) {
                    body = FileIoUtils.loadFileFromClasspath("./static" + requestTarget);
                }
                typeOfBodyContent = "text/" + requestTarget.split("\\.")[requestTarget.split("\\.").length-1];
            }
            response200Header(dos, body.length, typeOfBodyContent);
            responseBody(dos, body);

        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String typeOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + typeOfBodyContent + ";charset=utf-8 \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
