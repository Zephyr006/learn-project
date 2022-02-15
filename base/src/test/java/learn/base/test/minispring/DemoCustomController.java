package learn.base.test.minispring;

import learn.base.test.minispring.annotation.CustomAutowired;
import learn.base.test.minispring.annotation.CustomController;
import learn.base.test.minispring.annotation.CustomRequestMapping;
import learn.base.test.minispring.annotation.CustomRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Zephyr
 * @date 2022/1/3.
 */
@CustomController
@CustomRequestMapping(value = "/custom")
public class DemoCustomController {
    @CustomAutowired
    private ICustomService demoService;

    @CustomRequestMapping(value = "say")
    public String say(HttpServletRequest req, HttpServletResponse resp, @CustomRequestParam(value = "NAME") String name) {
        System.out.println("调用了controller的方法");
        String respStr = demoService.say(name);
        System.out.println("service 返回了值：" + respStr);
        return respStr;
    }
}
