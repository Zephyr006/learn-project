package learn.demo.webclient;

import learn.demo.webclient.impl.IBlogApi;
import learn.demo.webclient.impl.ProxyCreator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Zephyr
 * @date 2020/11/22.
 */
@SpringBootApplication
public class WebclientApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebclientApplication.class);
    }


    @Bean
    public FactoryBean<IBlogApi> blogApiFactoryBean(ProxyCreator proxyCreator) {
        return new FactoryBean<IBlogApi>() {
            // 返回代理的对象
            @Override
            public IBlogApi getObject() throws Exception {
                return (IBlogApi) proxyCreator.create(this.getObjectType());
            }

            @Override
            public Class<?> getObjectType() {
                return IBlogApi.class;
            }
        };
    }
}
