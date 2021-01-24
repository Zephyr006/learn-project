package learn.springcloud.config.client;

import learn.datasource.EnableDataServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Zephyr
 * @date 2020/10/9.
 */
@EnableDataServer
@SpringBootApplication
@EnableDiscoveryClient
public class ConfigClientApp {


    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApp.class);
    }
}
