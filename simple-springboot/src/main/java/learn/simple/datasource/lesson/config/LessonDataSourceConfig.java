package learn.simple.datasource.lesson.config;

import learn.simple.datasource.lesson.LessonDataSourceRegistrar;
import learn.simple.springboot.extradatasource.config.AbstractDataSourceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({LessonDataSourceRegistrar.class})
//@ComponentScan(basePackages = "learn.simple.datasource.lesson") //要求spring扫描指定路径下的注解，用于将DataSource注册为bean
@ConfigurationProperties(prefix = "datasource.lesson")
public class LessonDataSourceConfig extends AbstractDataSourceConfig {

    @Override
    public String getXmlMapperLocation() {
        return "";
    }
}
