package learn.datasource.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import learn.datasource.config.JkDataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;


/**
 * @author Zephyr
 * @date 2021/1/12.
 */
@Component
public class JkDataSource extends AbstractDataSource {

    @Autowired
    private JkDataSourceConfig config;


    @Override
    public DataSource getDataSource() {
        checkConfig(config);

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUserName());
        dataSource.setPassword(config.getPassword());
        dataSource.setEnable(config.isEnable());


        dataSource.setTestOnBorrow(config.isTestOnBorrow());
        dataSource.setTestWhileIdle(config.isTestWhileIdle());
        return dataSource;
    }

    @Override
    public String getMapperLocation() {
        return JkDataSourceConfig.MAPPER_LOCATION;
    }
}
