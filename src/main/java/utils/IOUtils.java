package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import webserver.HttpRequestLine;
import webserver.MyHttpRequest;

public class IOUtils {
    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    public static MyHttpRequest parseHttpRequest(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        HttpRequestLine httpRequestLine = new HttpRequestLine(line);
        Map<String, String> headers = parseHttpHeaders(bufferedReader);
        if (headers.containsKey("Content-Length")) {
            int length = Integer.parseInt(headers.get("Content-Length"));
            return new MyHttpRequest(httpRequestLine, headers, readData(bufferedReader, length));
        }
        return new MyHttpRequest(httpRequestLine, headers);
    }

    private static Map<String, String> parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        Map<String, String> headers = new HashMap<>();
        while (!isNullOrEmpty(line)) {
            String[] tokens = line.split(":");
            headers.put(tokens[0], tokens[1].trim());
            line = bufferedReader.readLine();
        }
        return headers;
    }

    private static boolean isNullOrEmpty(String line) {
        return line == null || "".equals(line);
    }
}
