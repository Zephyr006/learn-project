package learn;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.Assert;
import org.junit.Test;

import java.sql.*;
import java.util.Properties;

/**
 * @author Zephyr
 * @date 2021/3/7.
 */
public class MysqlConnectTest {
    private static final String driverClassName = "com.mysql.jdbc.Driver";
    private static final String url_template = "jdbc:mysql://%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";

    private static final String host = "192.168.2.136:3306"; // 192.168.2.136
    private static final String dbName = "crm";
    private static final String username = "root";
    private static final String password = "root";

    @Test
    public void testConnect() throws ClassNotFoundException {
        boolean needInsert = false;

        //1 加载MySql的驱动类
        Class<?> driverClass = Class.forName(driverClassName);
        //2 创建数据库的连接
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String url = String.format(url_template, host, dbName);
            System.out.println(url);
            connection = DriverManager.getConnection(url, username, password);
            // 推荐的连接可用性测试方案
            boolean isValid = connection.isValid(2);
            System.out.println("--- Mysql Connection is valid ? ---  " + isValid);

            //3 创建一个 Statement / PreparedStatement
            /*
                要执行SQL语句，必须获得java.sql.Statement实例，Statement实例分为以下3 种类型：
                1、执行静态SQL语句。通常通过Statement实例实现。
                2、执行动态SQL语句。通常通过PreparedStatement实例实现。
                3、执行数据库存储过程。通常通过CallableStatement实例实现。
            */
            //PreparedStatement preparedStatement = connection.prepareStatement();
            Assert.assertNotNull(connection);
            statement = connection.createStatement();
            Assert.assertNotNull(statement);
            //4 执行SQL语句：Statement接口提供了三种执行SQL语句的方法：executeQuery 、executeUpdate 和execute

            if (needInsert) {
                statement.execute("insert into good (name, price) values ('test_name', 123);");
            }
            resultSet = statement.executeQuery("select 1 as id;");
            //5 遍历处理返回结果：执行更新返回的是本次操作影响到的记录数；执行查询返回的结果是一个ResultSet对象。
            while (resultSet.next()) {
                long id = resultSet.getLong("id");
                //String name = resultSet.getString("name");
                //BigDecimal price = resultSet.getBigDecimal("price");
                //Timestamp createTime = resultSet.getTimestamp("create_time");
                //Timestamp updateTime = resultSet.getTimestamp("update_time");
                System.out.println(id);
                //System.out.println(createTime);
            }

        } catch (SQLException e) {
            System.err.println("数据库连接失败！");
            e.printStackTrace();
            System.exit(-1);
        } finally {
            //7 关闭资源
            close(resultSet);
            close(statement);
            close(connection);
        }



        System.out.println("--- Mysql connect successfully! ---");
    }

    @Test
    public void testConnectionByHikariCP() {
        HikariConfig hikariConfig = new HikariConfig(initHikariCPProps());

        try (final HikariDataSource dataSource = new HikariDataSource(hikariConfig);
             final Connection connection = dataSource.getConnection()) {
            // 推荐的连接可用性测试方案
            boolean isValid = connection.isValid(2);
            System.out.println("--- Mysql Connection is valid ? ---  " + isValid);

            // Statement 执行sql
            try (final Statement statement = connection.createStatement();
                 final ResultSet resultSet = statement.executeQuery("select 123 as id;")) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    System.out.println(id);
                }

                System.out.println("--- Mysql connect successfully! ---");
            }

            // PreparedStatement 执行sql （PreparedStatement设值下标从 1 开始）
            try (final PreparedStatement preparedStatement = connection.prepareStatement("select 111 as ?;")) {
                preparedStatement.setString(1, "id");
                try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        long id = resultSet.getLong("id");
                        System.out.println(id);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("=== HikariCP 连接数据库出现异常 ===");
            e.printStackTrace();
        }

    }


    private Properties initHikariCPProps() {
        // 最终会调用HikariConfig类中的 setXxx 方法设值（如setPassword），不存在对应方法则抛异常
        Properties properties = new Properties();
        properties.setProperty("driverClassName", driverClassName);
        properties.setProperty("jdbcUrl", String.format(url_template, host, dbName));
        properties.setProperty("username", username);
        properties.setProperty("password", password);
        // 连接只读数据库时配置为true， 保证安全
        properties.setProperty("readOnly", "true");
        // 一个连接idle状态的最大时长（毫秒），超时则被释放（retired），缺省:10分钟
        properties.setProperty("idleTimeout", "60000");
        // 等待连接池分配连接的最大时长（毫秒），超过这个时长还没可用的连接则发生SQLException， 缺省:30秒
        properties.setProperty("connectionTimeout", "9000");
        // 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，
        // 建议设置比数据库超时时长少30秒，参考MySQL wait_timeout参数（show variables like '%timeout%';）
        properties.setProperty("maxLifetime", "1800000");
        // 连接池中允许的最大连接数。缺省值：10；推荐的公式：((core_count * 2) + effective_spindle_count)
        properties.setProperty("maximumPoolSize", "10");
        // 连接池空闲连接的最小数量。默认minimumIdle与maximumPoolSize一样，为了性能考虑，不建议设置此值
        properties.setProperty("minimumIdle", "8");

        return properties;
    }

    private void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                System.err.println(closeable.toString() + " close error !");
                e.printStackTrace();
            }
        }
    }

}
