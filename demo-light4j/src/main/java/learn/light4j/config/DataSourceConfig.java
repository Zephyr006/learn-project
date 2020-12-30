package learn.light4j.config;

import lombok.Data;

/**
 * @author: cao
 * @time: 2020/12/7 7:11 下午
 */
@Data
public class DataSourceConfig {

    private String driverClassName;
    private String url;
    private String userName;
    private String password;
}
