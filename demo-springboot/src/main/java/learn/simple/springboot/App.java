package learn.simple.springboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Zephyr
 * @since 2021-11-27.
 */
@SpringBootApplication
        //(scanBasePackages = {"learn.simple.datasource.lesson", "learn.simple.datasource.question"})
public class App {

    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class)
                .run(args);
    }
}
