package learn.springcloud.config.client;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Zephyr
 * @since 2021-01-19.
 */
public class TestMybatisCache {

    public static void main(String[] args) throws IOException {
        String resource = "mybatis-config.xml";
        //读取mybatis-config配置文件
        InputStream inputStream = Resources.getResourceAsStream(resource);
        //创建SqlSessionFactory对象
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        //创建SqlSession对象
        SqlSession session = sqlSessionFactory.openSession();

        //UserMapper userMapper = session.getMapper(UserMapper.class);
        //List<LwUser> userList =  userMapper.selectUserAndJob();
        //List<LwUser> userList2 =  userMapper.selectUserAndJob();
    }
}
