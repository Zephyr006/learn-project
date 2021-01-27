package learn.datasource.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @date 2021-01-12
 */
@Getter
@Setter
@ToString(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "datasource.jk")
public class JkDataSourceConfig extends AbstractDataSourceConfig{

    public static final String MAPPER_LOCATION = "/mapper/jk/*.xml";


    private boolean enable = true;

    // 获取连接时执行validationQuery检测连接是否有效，这个配置会降低性能(在1.1.24版本已经解决了该问题)
    private boolean testOnBorrow = false;

    //如果连接空闲时间大于timeBetweenEvictionRunsMillis指定的毫秒，就会执行参数validationQuery指定的SQL来检测连接是否有效
    private boolean testWhileIdle = true;

}
