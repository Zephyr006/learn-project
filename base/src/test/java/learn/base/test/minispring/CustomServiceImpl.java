package learn.base.test.minispring;

import learn.base.test.minispring.annotation.CustomService;

/**
 * @author Zephyr
 * @since 2022-1-3.
 */
@CustomService
public class CustomServiceImpl implements ICustomService {

    @Override
    public String say(String param) {
        System.out.println("调用了自定义的service方法");
        return "Service say : " + param;
    }
}
