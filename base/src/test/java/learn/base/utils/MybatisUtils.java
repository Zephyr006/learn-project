package learn.base.utils;

import com.zaxxer.hikari.HikariDataSource;
import learn.base.test.business.mapper.TestMapper;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.LocalCacheScope;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Zephyr
 * @date 2021/5/26.
 */
public final class MybatisUtils {
    public static final boolean MAP_UNDERSCORE_TO_CAMEL_CASE = true;

    private static volatile SqlSessionFactory sqlSessionFactory;

    public static void main(String[] args) {
        String host = "39.106.73.19:3306";
        String dbName = "lesson";
        String username = "root";
        String pwd = "JustDoIt2019";
        Thread.currentThread().getContextClassLoader();
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
                        List<Class<?>> mappers = mapperClassPackage.isEmpty()
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
    private static SqlSessionFactory buildSqlSessionFactory(DataSource dataSource, List<Class<?>> mappers) {
        // 事务
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("development", transactionFactory, dataSource);

        Configuration configuration = new Configuration(environment);
        // 注入mapper
        Optional.ofNullable(mappers).ifPresent(ms -> ms.forEach(configuration::addMapper));
        // 将数据库字段的下划线命名转驼峰
        configuration.setMapUnderscoreToCamelCase(MAP_UNDERSCORE_TO_CAMEL_CASE);
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


    /**
     * 基于反射构建update语句中一个字段的设值语句，需要与{@link SQL#SET(java.lang.String)}配合使用
     * @param skipFields 要跳过构建sql的字段（不需要出现在更新的字段中），比如在updateById时不需要构建id的set语句
     * @return 形如："update_at=#{updateAt}"
     */
    public static String parseFieldToUpdateSqlCondition(Object entity, Field field, String... skipFields) {
        for (String skipField : skipFields) {
            if (skipField.equals(field.getName())) {
                return null;
            }
        }
        TableField annotation = field.getAnnotation(TableField.class);
        if (annotation != null) {
            if (!annotation.exist()) {
                return null;
            }
            String updateCondition = annotation.update();
            if (!updateCondition.isEmpty()) {
                return updateCondition;
            }
        }
        try {
            field.setAccessible(true);
            Object fieldValue = field.get(entity);
            // 字段值为空或者字符串为空串
            if ((fieldValue == null || (fieldValue instanceof String && ((String) fieldValue).isEmpty()))
                    && (annotation == null || !annotation.forceUpdate())) {
                return null;
            }
            return (MAP_UNDERSCORE_TO_CAMEL_CASE ? StringUtils.camelCaseToUnderline(field.getName()) : field.getName())
                    + "=#{" + field.getName() + '}';
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Documented
    @Target(value = ElementType.FIELD)
    @Retention(value = RetentionPolicy.RUNTIME)
    public @interface TableField {

        /**
         * 字段值（驼峰命名方式，该值可无）
         */
        //String value() default "";

        /**
         * 当该Field为类对象时, 可使用#{对象.属性}来映射到数据表.
         * <p>支持：@TableField(el = "role, jdbcType=BIGINT)</p>
         * <p>支持：@TableField(el = "role, typeHandler=com.baomidou.springcloud.typehandler.PhoneTypeHandler")</p>
         */
        //String el() default "";

        /**
         * 是否为数据库表字段
         * <p>默认 true 存在，false 不存在</p>
         */
        boolean exist() default true;

        /**
         * 如果为true，即使本字段的值为null，也会在update时使用null值更新此字段
         */
        boolean forceUpdate() default false;

        /**
         * 字段 where 实体查询比较条件
         * <p>默认 `=` 等值</p>
         */
        //String condition() default "";

        /**
         * 字段 update set 部分注入, 该注解优于 el 注解使用
         * <p>例如：@TableField(.. , update="%s+1") 其中 %s 会填充为字段</p>
         * <p>输出 SQL 为：update 表 set 字段=字段+1 where ...</p>
         * <p>例如：@TableField(.. , update="now()") 使用数据库时间</p>
         * <p>输出 SQL 为：update 表 set 字段=now() where ...</p>
         */
        String update() default "";

        /**
         * 是否进行 select 查询
         * <p>大字段可设置为 false 不加入 select 查询范围</p>
         */
        boolean select() default true;

    }

}
