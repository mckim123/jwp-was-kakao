package utils;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.StringReader;

public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }


    private String parserBody(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        StringBuilder sb = new StringBuilder();
        while (line != null) {
            sb.append(line).append("\n");
            line = bufferedReader.readLine();
        }
        String body = sb.toString();
        return body.substring(0, body.length()-1);
    }


    @Test
    public void parseBodyTest() throws IOException {
        String data = "{asdasdfasfd\n" +
                "asdfsadfsdaf\n" +
                "\n\n" +
                "}";

        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);
        parserBody(br);
    }
}
