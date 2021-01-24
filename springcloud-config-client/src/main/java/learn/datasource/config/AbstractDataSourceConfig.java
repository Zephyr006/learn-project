package learn.datasource.config;

import lombok.Data;

/**
 * @author Zephyr
 * @date 2021/1/12.
 */
@Data
public abstract class AbstractDataSourceConfig {

    String driverClassName;
    String url;
    String userName;
    String password;

}
