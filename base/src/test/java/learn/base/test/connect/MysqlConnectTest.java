package learn.base.test.connect;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import learn.base.BaseTest;
import learn.base.utils.FileLoader;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author Zephyr
 * @date 2021/3/7.
 */
public class MysqlConnectTest extends BaseTest {
    private static final String FILE_PATH = "conn-test.properties";
    private static final String driverClassName = "com.mysql.jdbc.Driver";
    private static final String url_template = "jdbc:mysql://%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";

    private static final String host = "192.168.2.136:3306"; // 192.168.2.136
    private static final String dbName = "crm";
    private static final String username = "root";
    private static final String password = "root";


    public static void main(String[] args) throws ClassNotFoundException {
        new MysqlConnectTest().testConnect();
    }

    public void testConnect() throws ClassNotFoundException {
        if (!checkContext()) {
            return;
        }
        boolean needInsert = false;
        final Properties props = FileLoader.loadProperties(FILE_PATH);

        //1 加载MySql的驱动类
        Class.forName(props.getOrDefault("jdbc.driverClassName", "com.mysql.jdbc.Driver").toString());
        //2 创建数据库的连接
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            String url = props.get("dataSource.jdbcUrl").toString().replace("{host}", props.get("host").toString());
            System.out.println(url);
            connection = DriverManager.getConnection(url, props.get("dataSource.username").toString(), props.get("dataSource.password").toString());
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
        if (!checkContext()) {
            return;
        }
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


    // 最终会调用HikariConfig类中的 setXxx 方法设值（如setPassword），不存在对应方法则抛异常
    private Properties initHikariCPProps() {
        final Properties properties = FileLoader.loadProperties(FILE_PATH);
        final Properties finalProps = new Properties();
        properties.forEach((key, value) -> {
            if (key.toString().startsWith("dataSource.")) {
                finalProps.put(key.toString().substring("dataSource.".length()),
                        value.toString().replace("{host}", properties.get("host").toString()));
            }
        });
        return finalProps;
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
