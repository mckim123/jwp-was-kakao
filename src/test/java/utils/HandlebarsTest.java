package utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import model.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HandlebarsTest {
    private static final Logger logger = LoggerFactory.getLogger(HandlebarsTest.class);

    @Test
    void name(){
        TemplateLoader loader = new ClassPathTemplateLoader();
        loader.setPrefix("/templates");
        loader.setSuffix(".html");
        Handlebars handlebars = new Handlebars(loader);

        assertDoesNotThrow(() -> {
            Template template = handlebars.compile("user/profile");

            User user = new User("javajigi", "password", "자바지기", "javajigi@gmail.com");
            String profilePage = template.apply(user);
            logger.debug("ProfilePage : {}", profilePage);
        });
    }
}
