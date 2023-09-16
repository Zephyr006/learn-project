package learn.dubbo.provider.service.impl;

import learn.dubbo.service.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author Zephyr
 * @date 2023-09-07
 */
@DubboService  // 通过这个注解配置可以基于 Spring Boot 去发布 Dubbo 服务
public class GreetingServiceImpl implements GreetingService {

    @Override
    public String sayHi(String name) {
        return "Hi, " + name;
    }
}
