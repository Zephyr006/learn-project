package learn.datasource.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import learn.datasource.config.BackendDataSourceConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;


/**
 * 后台数据源
 * @author: caoyanan
 * @time: 2020/10/29 5:50 下午
 */
//@Component
public class BackendDataSource extends AbstractDataSource {

    @Autowired
    private BackendDataSourceConfig config;


    @Override
    public DataSource getDataSource() {
        checkConfig(config);

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(config.getDriverClassName());
        dataSource.setUrl(config.getUrl());
        dataSource.setUsername(config.getUserName());
        dataSource.setPassword(config.getPassword());
        dataSource.setTestOnBorrow(true);
        return dataSource;
    }


    @Override
    public String getMapperLocation() {
        return BackendDataSourceConfig.MAPPER_LOCATION;
    }




}
