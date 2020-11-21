package learn.demo.webflux;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Zephyr
 * @date 2020/11/19.
 */
@SpringBootApplication
public class WebfluxApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(WebfluxApplication.class)
                .bannerMode(Banner.Mode.CONSOLE)
                .web(WebApplicationType.REACTIVE)
                .profiles("default")
                .headless(true)
                .listeners()
                .build();
        application.run(args);
    }

}
