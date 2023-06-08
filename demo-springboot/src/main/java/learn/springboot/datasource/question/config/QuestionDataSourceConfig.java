package learn.springboot.datasource.question.config;

import learn.springboot.datasource.question.QuestionDataSourceRegistrar;
import learn.springboot.extradatasource.config.AbstractDataSourceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * @since 2021-01-12
 */
@Configuration
@Import({QuestionDataSourceRegistrar.class})
//@ComponentScan(basePackages = "learn.simple.springboot.datasource.question")
@ConfigurationProperties(prefix = "datasource.question")
public class QuestionDataSourceConfig extends AbstractDataSourceConfig {

    @Override
    public String getXmlMapperLocation() {
        return "/mapper/question/*.xml";
    }

    //private boolean enable = true;

    // 获取连接时执行validationQuery检测连接是否有效，这个配置会降低性能(在1.1.24版本已经解决了该问题)
    //private boolean testOnBorrow = false;

    //如果连接空闲时间大于timeBetweenEvictionRunsMillis指定的毫秒，就会执行参数validationQuery指定的SQL来检测连接是否有效
    //private boolean testWhileIdle = true;

}
