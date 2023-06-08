package learn.springboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Zephyr
 * @since 2021-11-27.
 */
@SpringBootApplication
        //(scanBasePackages = {"learn.simple.springboot.datasource.lesson", "learn.simple.springboot.datasource.question"})
public class App {

    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class)
                .run(args);
    }
}
