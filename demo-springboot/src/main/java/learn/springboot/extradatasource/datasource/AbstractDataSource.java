package learn.springboot.extradatasource.datasource;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import learn.springboot.extradatasource.config.AbstractDataSourceConfig;
import learn.springboot.extradatasource.plugin.LoggingInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

@Slf4j
public abstract class AbstractDataSource {


    protected final AbstractDataSourceConfig config;
    private MybatisMapperRegistry mapperRegistry;
    private SqlSession sqlSession;


    public AbstractDataSource(AbstractDataSourceConfig config) {
        this.config = config;
        if (!register()) {
            log.error(this.getClass().getSimpleName() + " : 注册了bean却没有配置数据源参数，请检查配置。");
        }
    }

    protected DataSource buildDataSource() {
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("jdbcUrl", config.getUrl());
        properties.setProperty("username", config.getUserName());
        properties.setProperty("password", config.getPassword());
        // 连接只读数据库时配置为true， 保证安全
        //properties.setProperty("readOnly", "true");
        // 是否自动提交事务
        //properties.setProperty("autoCommit", "false");
        // 设置在创建新连接时将在所有新连接上执行的SQL字符串，然后再将其添加到池中。如果此查询失败，它将被视为失败的连接尝试。
        properties.setProperty("connectionInitSql", "SET NAMES utf8mb4");
        // 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        properties.setProperty("idleTimeout", "60000");
        // 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，
        // 建议设置比数据库超时时长少30秒，参考MySQL wait_timeout参数（show variables like '%timeout%';）
        properties.setProperty("maxLifetime", "1800000");
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        properties.setProperty("connectionTimeout", "15000");
        // 连接池中允许的最大连接数。缺省值：10；推荐的公式：((core_count * 2) + effective_spindle_count)
        Optional.ofNullable(config.getMaxActive()).map(String::valueOf)
                .ifPresent(maxActive -> properties.setProperty("maximumPoolSize", maxActive));
        // 连接池空闲连接的最小数量。默认minimumIdle与maximumPoolSize一样，为了性能考虑，不建议设置此值
        //properties.setProperty("minimumIdle", "8");

        HikariConfig hikariConfig = new HikariConfig(properties);
        return new HikariDataSource(hikariConfig);
    }


    /**
     * 提供数据源
     * @return
     */
    protected DataSource getDataSource() {
        checkConfig();
        return register() ? buildDataSource() : null;
    }

    /**
     * mapper.xml文件的位置
     * @return
     */
    protected String getMapperLocation() {
        return config.getXmlMapperLocation();
    }

    /**
     * 是否注册
     */
    public Boolean register() {
        return Objects.nonNull(config.getDriverClassName())
                || Objects.nonNull(config.getPassword())
                || Objects.nonNull(config.getUrl())
                || Objects.nonNull(config.getUserName());
    }



    public <T> T getMapperInstance(Class<T> mapper) {
        if (Objects.isNull(mapperRegistry)
                || Objects.isNull(sqlSession)) {
            initMapperRegistry();
        }
        boolean hasMapper = mapperRegistry.hasMapper(mapper);
        if (!hasMapper) {
            mapperRegistry.addMapper(mapper);
        }
        return sqlSession.getMapper(mapper);
    }

    private void initMapperRegistry() {
        SqlSessionFactory sqlSessionFactory;
        try {
            sqlSessionFactory = buildSqlSessionFactory();
        } catch (Exception e) {
            log.error("dataSource init failed", e);
            throw new RuntimeException(e);
        }
        this.mapperRegistry = (MybatisMapperRegistry) sqlSessionFactory.getConfiguration().getMapperRegistry();
        this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
    }

    private SqlSessionFactory buildSqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(getDataSource());
        String mapperLocation = getMapperLocation();
        if (mapperLocation != null && !mapperLocation.isEmpty()) {
            sqlSessionFactory.setMapperLocations(
                    new PathMatchingResourcePatternResolver().getResources(mapperLocation));
        }
        Interceptor[] plugins = new Interceptor[2];
        plugins[0] = new LoggingInterceptor();
        plugins[1] = paginationInterceptor();
        sqlSessionFactory.setPlugins(plugins);
        return sqlSessionFactory.getObject();
    }

    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor innerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        innerInterceptor.setMaxLimit(-1L);
        interceptor.addInnerInterceptor(innerInterceptor);
        return interceptor;
    }

    protected boolean checkConfig() {
        checkNotNull(config.getDriverClassName(), "driver-class-name can not be null");
        checkNotNull(config.getUrl(), "url can not be null");
        checkNotNull(config.getUserName(), "username can not be null");
        checkNotNull(config.getPassword(), "password can not be null");
        return true;
    }

    private void checkNotNull(String value, String message) {
        if (Objects.isNull(value) || value.isEmpty()) {
            throw new RuntimeException(message);
        }
    }

}
