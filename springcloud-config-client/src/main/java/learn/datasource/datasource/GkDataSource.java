package learn.datasource.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import learn.datasource.config.GkDataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @author: caoyanan
 * @time: 2021/1/14 4:40 下午
 */
@Component
public class GkDataSource extends AbstractDataSource {

    public static final String MAPPER_LOCATION = "/mapper/gk/*.xml";

    @Autowired
    private GkDataSourceConfig config;


    @Override
    public DataSource getDataSource() {
        checkConfig(config);

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUserName());
        dataSource.setPassword(config.getPassword());
        dataSource.setTestWhileIdle(true);
        return dataSource;
    }


    @Override
    public String getMapperLocation() {
        return MAPPER_LOCATION;
    }

}
