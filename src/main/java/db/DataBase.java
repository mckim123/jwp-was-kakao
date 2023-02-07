package db;

import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
import model.User;

public class DataBase {

    private static final Map<String, User> users = new HashMap<>();

    private DataBase() {
        throw new IllegalAccessError();
    }

    public static void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public static User findUserById(String userId) {
        return users.get(userId);
    }

    public static Collection<User> findAll() {
        return users.values();
    }
}
