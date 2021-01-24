package learn.datasource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @author: caoyanan
 * @time: 2020/10/29 5:51 下午
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "datasource.backend")
public class BackendDataSourceConfig extends AbstractDataSourceConfig {

    public static final String MAPPER_LOCATION = "/mapper/backend/*.xml";
}
