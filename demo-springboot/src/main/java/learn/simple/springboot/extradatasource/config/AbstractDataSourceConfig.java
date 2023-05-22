package learn.simple.springboot.extradatasource.config;

import lombok.Data;

/**
 * @author Zephyr
 * @date 2021/1/12.
 */
@Data
public abstract class AbstractDataSourceConfig {

    public abstract String getXmlMapperLocation();

    String driverClassName;
    String url;
    String userName;
    String password;

    Integer minIdle;
    Integer maxActive;

}
