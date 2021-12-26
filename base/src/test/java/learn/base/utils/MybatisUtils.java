package learn.base.utils;

import com.zaxxer.hikari.HikariDataSource;
import learn.base.test.business.mapper.TestMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Zephyr
 * @date 2021/5/26.
 */
public final class MybatisUtils {

    private static volatile SqlSessionFactory sqlSessionFactory;

    public static void main(String[] args) {
        String host = "39.106.73.19:3306";
        String dbName = "crm";
        String username = "root";
        String pwd = "JustDoIt2019";

        HikariDataSource dataSource = new HikariDataSource(HikariConfigUtil.buildHikariConfig(host, dbName, username, pwd));
        SqlSessionFactory sqlSessionFactory = getSqlSessionFactory(dataSource, "learn.base.test.business.mapper");
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            TestMapper testMapper = sqlSession.getMapper(TestMapper.class);
            List<Object> result = testMapper.selectAll(1, true);
        }
    }

    /**
     * SqlSessionFactory 一旦被创建就应该在应用的运行期间一直存在，最简单的就是使用单例模式或者静态单例模式。
     */
    public static SqlSessionFactory getSqlSessionFactory(DataSource dataSource, String mapperClassPackage) {
        if (sqlSessionFactory == null) {
            synchronized (MybatisUtils.class) {
                if (sqlSessionFactory == null) {
                    assert dataSource != null;
                    assert mapperClassPackage != null;
                    try {
                        List<Class<?>> mappers = StringUtils.isEmpty(mapperClassPackage)
                                ? Collections.emptyList() : Reflections.getClasses(mapperClassPackage);
                        sqlSessionFactory = buildSqlSessionFactory(dataSource, mappers);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return sqlSessionFactory;
    }

    /**
     * 不使用xml构建 SqlSessionFactory
     * 使用示例
     * try (SqlSession session = sqlSessionFactory.openSession()) {
     *   BlogMapper mapper = session.getMapper(BlogMapper.class);
     *   Blog blog = mapper.selectBlog(101);
     * }
     */
    public static SqlSessionFactory buildSqlSessionFactory(DataSource dataSource, List<Class<?>> mappers) {
        // 事务
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);

        Configuration configuration = new Configuration(environment);
        // 注入mapper
        Optional.ofNullable(mappers).ifPresent(ms -> ms.forEach(configuration::addMapper));
        // 将数据库字段的下划线命名转驼峰
        configuration.setMapUnderscoreToCamelCase(true);
        // 超时时间，它决定驱动等待数据库响应的 秒数。
        configuration.setDefaultStatementTimeout(8);
        // 为驱动的结果集获取数量（fetchSize）设置一个提示值。此参数只可以在查询设置中被覆盖。
        configuration.setDefaultFetchSize(3000);
        // 关闭缓存
        configuration.setCacheEnabled(false);

        // 注入拦截器
        // configuration.addInterceptor(new MybatisShardInterceptor());
        // configuration.addInterceptor(paginationInterceptor());
        // 一级缓存/本地缓存将会在执行update、事务提交或回滚，以及关闭 session 时清空。
        // 对于某个对象，MyBatis 将返回在本地缓存中唯一对象的引用。因此不要对 MyBatis 所返回的对象作出更改
        configuration.setLocalCacheScope(LocalCacheScope.STATEMENT);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        return sqlSessionFactory;
    }

    // private static PaginationInterceptor paginationInterceptor() {
    //     PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
    //     paginationInterceptor.setDbType(DbType.MYSQL);
    //     paginationInterceptor.setCountSqlParser(new JsqlParserCountOptimize());
    //     return paginationInterceptor;
    // }
}
