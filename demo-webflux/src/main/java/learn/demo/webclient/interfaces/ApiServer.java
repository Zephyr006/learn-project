package learn.demo.webclient.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务器相关信息，应用在XxxApi的类名上
 *
 * @author Zephyr
 * @date 2020/11/22.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiServer {

    /**
     * 要请求的目标url地址
     */
    String baseUrl();

}
