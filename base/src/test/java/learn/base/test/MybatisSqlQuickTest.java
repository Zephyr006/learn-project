package learn.base.test;

import learn.base.test.business.mapper.LessonWatchTimeLogMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Zephyr
 * @date 2023-09-12
 */
public class MybatisSqlQuickTest {
    static LessonWatchTimeLogMapper mapper;

    /**
     * 参考自 { @link https://juejin.cn/post/7271942371638214708}
     *  Method beforeClass() should be static
     */
    @BeforeClass
    public static void beforeClass(){
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder()
            .build(MybatisSqlQuickTest.class.getClassLoader().getResourceAsStream("mybatis-config.xml"));

        // 在生成的testcase中有一个setUp方法，将SqlSessionFactoryBuilder改成MybatisSqlSessionFactoryBuilder
        // 即可测试mybatisplus自带的一些方法

        // you can use builder.openSession(false) to not commit to database
        // 加入interceptor支持PageHelper分页插件
        // PageInterceptor interceptor = new PageInterceptor();
        // sessionFactory.getConfiguration().addInterceptor(interceptor);

        // ==== mybatisplus添加分页插件和乐观锁插件 ====
        // SqlSessionFactory builder = new MybatisSqlSessionFactoryBuilder().build(MybatisQuickTest.class
        //     .getClassLoader().getResourceAsStream("mybatis-config.xml"));
        // final MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // builder.getConfiguration().addInterceptor(interceptor);

        boolean autoCommit = true;
        mapper = sessionFactory.getConfiguration().getMapper(
            LessonWatchTimeLogMapper.class, sessionFactory.openSession(autoCommit));
    }

    @Test
    public void testMybatisWithoutSpring(){
        assert mapper != null;
    }
}
