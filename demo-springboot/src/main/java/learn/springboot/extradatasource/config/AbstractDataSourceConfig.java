package learn.springboot.extradatasource.config;

import lombok.Data;

/**
 * @author Zephyr
 * @since 2021-01-12.
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
