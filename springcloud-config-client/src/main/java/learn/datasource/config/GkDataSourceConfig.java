package learn.datasource.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: caoyanan
 * @time: 2021/1/14 4:43 下午
 */
@Configuration
@ConfigurationProperties(prefix = "datasource.gk")
public class GkDataSourceConfig extends AbstractDataSourceConfig {

}
