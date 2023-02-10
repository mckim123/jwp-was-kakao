package webserver;

import static utils.IOUtils.parseHttpRequest;

import db.Session;
import db.SessionManager;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import org.springframework.http.HttpStatus;
import utils.FileIoUtils;

public class RequestHandler implements Runnable {
    private static final String SERVLET_LOCATION_PREFIX = "myservlet.";
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final SessionManager sessionManager = SessionManager.getInstance();

    //실제 서블릿을 매핑
    private static final Map<String, MyHttpServlet> servlets = new ConcurrentHashMap<>();

    //서블릿 이름을 매핑
    private static final Map<String, String> servletMappings = new ConcurrentHashMap<>();
    private static final Set<String> loginExcluded = new HashSet<>();
    private static final Set<String> loginRequired = new HashSet<>();

    static {
        servletMappings.put("/", "DefaultServlet");
        servletMappings.put("/user/login", "UserLoginServlet");
        servletMappings.put("/user/create", "UserCreateServlet");
        servletMappings.put("/user/list", "UserListServlet");

        loginExcluded.add("/user/login.html");
        loginExcluded.add("/user/login_failed.html");
        loginExcluded.add("/user/form.html");

        loginRequired.add("/user/list.html");
        loginRequired.add("/user/list");
        loginRequired.add("/user/profile.html");
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
        } catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | InstantiationException |
                 IllegalAccessException | URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void service(MyHttpRequest request, MyHttpResponse response)
            throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, URISyntaxException {
        handleCookie(request, response);
        validateAuthentication(request, response);
        if (response.isStatusAssigned()) {
            return;
        }
        MyHttpServlet httpServlet = findServlet(request.getRequestPath());
        httpServlet.service(request, response);
    }

    private void handleCookie(MyHttpRequest request, MyHttpResponse response) {
        HttpCookie httpCookie = request.getHttpCookie();
        if (httpCookie == null || sessionManager.findSession(httpCookie.getJSessionId()) == null) {
            UUID uuid = UUID.randomUUID();
            httpCookie = new HttpCookie(uuid.toString());
            response.addHeader("Set-Cookie", httpCookie.toString());
            sessionManager.add(new Session(uuid.toString()));
        }
        httpCookie.validatePath(request.getRequestPath());
    }

    private void validateAuthentication(MyHttpRequest request, MyHttpResponse response) {
        if (!isUserLoggedIn(request, response) && isLoginRequired(request.getRequestPath())) {
            redirectToLoginPage(response);
        }
        if (isUserLoggedIn(request, response) && isLoginExcluded(request.getRequestPath())) {
            redirectToIndexPage(response);
        }
    }

    private boolean isLoginExcluded(String requestPath) {
        return loginExcluded.contains(requestPath);
    }

    private boolean isLoginRequired(String requestPath) {
        return loginRequired.contains(requestPath);
    }

    private void redirectToIndexPage(MyHttpResponse response) {
        response.setStatus(HttpStatus.SEE_OTHER);
        response.addHeader("Location", "/index.html");
    }

    private void redirectToLoginPage(MyHttpResponse response) {
        response.setStatus(HttpStatus.SEE_OTHER);
        response.addHeader("Location", "/user/login.html");
    }

    private boolean isUserLoggedIn(MyHttpRequest request, MyHttpResponse response) {
        try {
            Session session = sessionManager.findSession(findJSessionId(request, response));
            return !Objects.isNull(session.getAttribute("User"));
        } catch (Exception e) {
            return false;
        }
    }

    private String findJSessionId(MyHttpRequest request, MyHttpResponse response) {
        try {
            return request.getJSessionId();
        } catch (NullPointerException e) {
            return response.getJSessionId();
        }
    }

    private MyHttpServlet findServlet(String requestTarget)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, URISyntaxException {
        if (isRequestTargetFileType(requestTarget)) {
            return getInstanceOf(getServletNameOfFile(requestTarget));
        }
        String servletName = servletMappings.get(requestTarget);
        return getInstanceOf(servletName);
    }

    private String getServletNameOfFile(String requestTarget) throws URISyntaxException {
        if (FileIoUtils.isFileExisting("./static" + requestTarget)) {
            return "StaticFileServlet";
        }
        if (FileIoUtils.isFileExisting("./templates" + requestTarget)) {
            return "TemplateFileServlet";
        }
        throw new IllegalArgumentException(requestTarget);
    }

    private static MyHttpServlet getInstanceOf(String servletName)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (!servlets.containsKey(servletName)) {
            MyHttpServlet servlet = (MyHttpServlet) Class.forName(SERVLET_LOCATION_PREFIX + servletName)
                    .getDeclaredConstructor().newInstance();
            servlet.init();
            servlets.put(servletName, servlet);
        }
        return servlets.get(servletName);
    }

    private static boolean isRequestTargetFileType(String requestTarget) {
        return requestTarget.contains(".");
    }
}
