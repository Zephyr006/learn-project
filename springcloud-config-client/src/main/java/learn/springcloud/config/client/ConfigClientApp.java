package learn.springcloud.config.client;

import learn.datasource.EnableDataServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Zephyr
 * @date 2020/10/9.
 */
@EnableDataServer
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"learn.springcloud.config.client", "learn.datasource"})
@MapperScan(basePackages = {"learn.springcloud.config.client.mapper", "learn.datasource.mapper"})
public class ConfigClientApp {


    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApp.class);
    }
}
