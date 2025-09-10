package learn.ai;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
//(scanBasePackages = {"learn.simple.springboot.datasource.lesson", "learn.simple.springboot.datasource.question"})
public class App {

    public static void main(String[] args) {
        new SpringApplicationBuilder(App.class)
                .run(args);
    }
}
