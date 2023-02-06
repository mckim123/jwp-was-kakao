package utils;

import com.github.jknack.handlebars.internal.lang3.ObjectUtils.Null;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileIoUtils {
    public static byte[] loadFileFromClasspath(String filePath) throws IOException, URISyntaxException {
        Path path = Paths.get(FileIoUtils.class.getClassLoader().getResource(filePath).toURI());
        return Files.readAllBytes(path);
    }

    public static byte[] loadFileFromRequestTarget(String requestTarget) throws IOException, URISyntaxException {
        try {
            return loadFileFromClasspath("./templates" + requestTarget);
        } catch (NullPointerException e) {
            return loadFileFromClasspath("./static" + requestTarget);
        }
    }
}
