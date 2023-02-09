package webserver;

import static utils.IOUtils.parseHttpRequest;

import db.Session;
import db.SessionManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import myservlet.MyHttpServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import utils.StringUtils;

public class RequestHandler implements Runnable {
    private static final String SERVLET_LOCATION_PREFIX = "myservlet.";
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    public static SessionManager sessionManager = SessionManager.getInstance();

    //실제 서블릿을 매핑
    private static final Map<String, MyHttpServlet> servlets = new ConcurrentHashMap<>();

    //서블릿 이름을 매핑
    private static final Map<String, String> servletMappings = new ConcurrentHashMap<>();

    static {
        servletMappings.put("/", "DefaultServlet");
        servletMappings.put("/user/login", "UserLoginServlet");
        servletMappings.put("/user/create", "UserCreateServlet");
        servletMappings.put("/user/list", "UserListServlet");
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
        handleCookie(request, response);
        MyHttpServlet httpServlet = findServlet(request.getRequestPath());
        httpServlet.service(request, response);
    }

    private void handleCookie(MyHttpRequest request, MyHttpResponse response) {
        HttpCookie httpCookie = request.getHttpCookie();
        if (httpCookie == null) {
            UUID uuid = UUID.randomUUID();
            httpCookie = new HttpCookie(uuid.toString());
            response.addHeader("Set-Cookie", httpCookie.toString());
            sessionManager.add(new Session(uuid.toString()));
        }
        httpCookie.validatePath(request.getRequestPath());
    }

    private MyHttpServlet findServlet(String requestTarget)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        if (isRequestTargetFileType(requestTarget)) {
            return getInstanceOf("StaticFileServlet");
        }
        String servletName = servletMappings.get(requestTarget);
        return getInstanceOf(servletName);
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

    private static boolean isRequestTargetFileType(String requestTarget) {
        return requestTarget.contains(".");
    }
}
