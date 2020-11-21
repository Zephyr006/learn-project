package learn.springcloud.config.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zephyr
 * @date 2020/10/9.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ConfigClientApp {


    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApp.class);
    }
}
