package learn.datasource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: caoyanan
 * @time: 2021/1/14 4:57 下午
 */
@Configuration
@ConfigurationProperties(prefix = "sharding")
@Data
public class ShardingConfig {

    private Integer userQuestionLog;

    private Integer submitLog;

    private Integer userQuestion;
}
