package utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;

public class StringUtils {
    private StringUtils() {
        throw new IllegalAccessError();
    }

    public static boolean isEmpty(@Nullable Object str) {
        return (str == null || "".equals(str));
    }

    public static Map<String, String> stringToMap(String str) {
        return stringToMap(str, "&", "=");
    }

    public static Map<String, String> stringToMap(String str, String delim1, String delim2) {
        Map<String, String> map = new HashMap<>();
        Arrays.stream(str.split(delim1))
                .forEach(x -> map.put(x.split(delim2)[0], x.split(delim2)[1]));
        return map;
    }

    public static Map<String, String> stringToMapWithTrim(String str, String delim1, String delim2) {
        Map<String, String> map = new HashMap<>();
        Arrays.stream(str.split(delim1))
                .forEach(x -> map.put(x.split(delim2)[0].trim(), x.split(delim2)[1].trim()));
        return map;
    }
}
