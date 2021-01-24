package learn.datasource.datasource;

import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import learn.datasource.config.AbstractDataSourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Objects;

/**
 * @author: caoyanan
 * @time: 2020/10/29 5:49 下午
 */
@Slf4j
public abstract class AbstractDataSource {

    /**
     * 提供数据源
     * @return
     */
    public abstract DataSource getDataSource();

    /**
     * mapper文件位置
     * @return
     */
    public abstract String getMapperLocation();



    private MybatisMapperRegistry mapperRegistry;
    private SqlSession sqlSession;

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
        if (StringUtils.isNotBlank(mapperLocation)) {
            sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().
                    getResources(mapperLocation));
        }
        return sqlSessionFactory.getObject();
    }

    protected void checkConfig(AbstractDataSourceConfig config) {
        checkNotNull(config.getDriverClassName(), "driver-class-name can not be null");
        checkNotNull(config.getUrl(), "url can not be null");
        checkNotNull(config.getUserName(), "username can not be null");
        checkNotNull(config.getPassword(), "password can not be null");
    }

    private void checkNotNull(String name, String message) {
        if (Objects.isNull(name)) {
            throw new RuntimeException(message);
        }
    }

}
