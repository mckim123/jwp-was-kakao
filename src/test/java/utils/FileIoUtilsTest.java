package utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FileIoUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(FileIoUtilsTest.class);

    @Test
    void loadFileFromClasspath() {
        assertDoesNotThrow(() -> {
            byte[] body = FileIoUtils.loadFileFromClasspath("./templates/index.html");
            logger.debug("file : {}", new String(body));
        });
    }
}
