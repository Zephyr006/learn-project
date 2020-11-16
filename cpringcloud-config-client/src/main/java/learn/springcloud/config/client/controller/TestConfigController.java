package learn.springcloud.config.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zephyr
 * @date 2020/10/9.
 */
@RestController
public class TestConfigController {
    @Value("${customer.inviteDay}")
    Integer inviteValidDay;


    @GetMapping("testConfig")
    public String getConfig() {
        System.out.println("testttt");
        return String.valueOf(inviteValidDay);
    }
}
