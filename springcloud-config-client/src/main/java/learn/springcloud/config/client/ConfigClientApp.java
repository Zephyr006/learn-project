package learn.springcloud.config.client;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Zephyr
 * @date 2020/10/9.
 */
@EnableDiscoveryClient
@MapperScan(basePackages = {"learn.springcloud.config.client.mapper"})
public class ConfigClientApp {


    public static void main(String[] args) {
        SpringApplication.run(ConfigClientApp.class);
    }
}
