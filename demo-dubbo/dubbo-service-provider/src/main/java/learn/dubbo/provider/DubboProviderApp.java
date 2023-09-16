package learn.dubbo.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Zephyr
 * @date 2023-09-07
 */
// 启动 Dubbo 相关配置并指定 Spring Boot 扫描包路径
@EnableDubbo
@SpringBootApplication
public class DubboProviderApp {

    public static void main(String[] args) {
        SpringApplication.run(DubboProviderApp.class, args);
    }
}
