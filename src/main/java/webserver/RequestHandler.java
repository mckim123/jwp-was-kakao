package webserver;

import static utils.IOUtils.parseHttpRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import myservlet.MyHttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private static final String SERVLET_LOCATION_PREFIX = "myservlet.";
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    //실제 서블릿을 매핑
    private static final Map<String, MyHttpServlet> servlets = new ConcurrentHashMap<>();

    //서블릿 이름을 매핑
    private static final Map<String, String> servletMappings = new ConcurrentHashMap<>();

    static {
        servletMappings.put("/user", "UserServlet");
        servletMappings.put("/user/create", "UserCreateServlet");
        servletMappings.put("/", "DefaultServlet");
    }

    private static MyHttpServlet getInstanceOf(String servletName)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!servlets.containsKey(servletName)) {
            MyHttpServlet servlet = (MyHttpServlet) Class.forName(SERVLET_LOCATION_PREFIX + servletName).getDeclaredConstructor().newInstance();
            servlet.init();
            servlets.put(servletName, servlet);
        }
        return servlets.get(servletName);
    }

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
            MyHttpRequest request = parseHttpRequest(bufferedReader);
            DataOutputStream dos = new DataOutputStream(out);
            MyHttpResponse response = new MyHttpResponse();
            service(request, response);
            response.writeResponse(dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void service(MyHttpRequest request, MyHttpResponse response)
            throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        MyHttpServlet httpServlet = findServlet(request.getRequestPath());
        httpServlet.service(request, response);
    }

    private MyHttpServlet findServlet(String requestTarget)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (isRequestTargetFile(requestTarget)) {
            return getInstanceOf("StaticFileServlet");
        }
        String servletName = servletMappings.get(requestTarget);
        return getInstanceOf(servletName);
    }

    private static boolean isRequestTargetFile(String requestTarget) {
        return requestTarget.contains(".");
    }
}
