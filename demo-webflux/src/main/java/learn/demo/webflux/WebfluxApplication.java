package learn.demo.webflux;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author Zephyr
 * @since 2020-11-19.
 */
@SpringBootApplication
public class WebfluxApplication {

    /**
     * 如果想要保持 Spring Boot WebFlux 的自动配置功能，并且想添加额外的 WebFlux 配置项，可以自定义 @Configuration 配置类，但不要添加 @EnableWebFlux 注解。
     *
     * 如果你想要完全控制 WebFlux，你可以定义@Configuration 配置类，并且添加 @EnableWebFlux. 注解。
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(WebfluxApplication.class)
                .bannerMode(Banner.Mode.CONSOLE)
                .web(WebApplicationType.REACTIVE)
                .profiles("webflux")
                .headless(true)
                .listeners()
                .build();
        application.run(args);
    }

}
