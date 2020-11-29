package learn.demo.webclient;

import learn.demo.webclient.api.IBlogApi;
import learn.demo.webclient.interfaces.impl.JdkProxyCreator;
import learn.demo.webclient.interfaces.ProxyCreator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author Zephyr
 * @date 2020/11/22.
 */
@SpringBootApplication
public class WebclientApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(WebclientApplication.class)
                .profiles("webclient")
                .headless(true)
                .build();
        application.run(args);
    }

    /**
     * jdk代理类创建的工具类
     */
    @Bean
    public ProxyCreator jdkProxyCreator() {
        return new JdkProxyCreator();
    }

    /**
     * 提供FactoryBean，用于生成需要代理的对象实例
     */
    @Bean
    public FactoryBean<IBlogApi> blogApiFactoryBean(ProxyCreator proxyCreator) {
        return new FactoryBean<IBlogApi>() {
            // 返回代理的对象实例
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
