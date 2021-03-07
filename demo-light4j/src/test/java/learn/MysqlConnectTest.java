package learn;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.*;

/**
 * @author Zephyr
 * @date 2021/3/7.
 */
public class MysqlConnectTest {
    private static final String driverClassName = "com.mysql.jdbc.Driver";
    private static final String url_template = "jdbc:mysql://%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";


    @Test
    public void testConnect() throws ClassNotFoundException, SQLException {
        String host = "192.168.2.136:3306";
        String dbName = "db_learn";
        String username = "root";
        String password = "root";
        boolean needInsert = true;

        //1 加载MySql的驱动类
        Class<?> driverClass = Class.forName(driverClassName);
        //2 创建数据库的连接
        Connection conn = null;
        try {
            String url = String.format(url_template, host, dbName);
            System.out.println(url);
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("数据库连接失败！");
            e.printStackTrace();
            System.exit(-1);
        }

        //3 创建一个 Statement / PreparedStatement
        /*
            要执行SQL语句，必须获得java.sql.Statement实例，Statement实例分为以下3 种类型：
            1、执行静态SQL语句。通常通过Statement实例实现。
            2、执行动态SQL语句。通常通过PreparedStatement实例实现。
            3、执行数据库存储过程。通常通过CallableStatement实例实现。
        */
        //PreparedStatement preparedStatement = conn.prepareStatement();
        Assert.assertNotNull(conn);
        Statement statement = conn.createStatement();
        Assert.assertNotNull(statement);
        //4 执行SQL语句：Statement接口提供了三种执行SQL语句的方法：executeQuery 、executeUpdate 和execute

        if (needInsert) {
            statement.execute("insert into good (name, price) values ('test_name', 123);");
        }
        ResultSet resultSet = statement.executeQuery("select * from good limit 10;");
        //5 遍历处理返回结果：执行更新返回的是本次操作影响到的记录数；执行查询返回的结果是一个ResultSet对象。
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            BigDecimal price = resultSet.getBigDecimal("price");
            Timestamp createTime = resultSet.getTimestamp("create_time");
            Timestamp updateTime = resultSet.getTimestamp("update_time");
            System.out.println(id);
            System.out.println(createTime);
        }

        //7 关闭资源
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (conn != null) {
            conn.close();
        }

        System.out.println("--- Mysql connect successfully! ---");
    }

}
