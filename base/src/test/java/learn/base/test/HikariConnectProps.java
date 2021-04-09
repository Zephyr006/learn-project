package learn.base.test;

import java.util.Properties;

/**
 * @author Zephyr
 * @date 2021/4/8.
 */
public class HikariConnectProps {


    public static Properties initProps() {
        String host = "192.168.2.136";
        String dbName = "relation";
        String username = "root";
        String password = "root";
        return HikariConnectProps.initProps(host, dbName, username, password);
    }

    /**
     * 用于Hikari数据库连接池的参数初始化（Properties类的参数格式为驼峰格式，spring配置文件为下划线格式）
     * 最终会调用HikariConfig类中的 setXxx 方法设值（如setPassword），不存在对应方法则抛异常
     */
    public static Properties initProps(String host, String dbName, String username, String password) {
        Properties properties = new Properties();
        String url_template = "jdbc:mysql://%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true";

        properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        properties.setProperty("jdbcUrl", String.format(url_template, host, dbName));
        properties.setProperty("username", username);
        properties.setProperty("password", password);
        // 连接只读数据库时配置为true， 保证安全
        properties.setProperty("readOnly", "true");
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
        properties.setProperty("maximumPoolSize", "123");
        // 连接池空闲连接的最小数量。默认minimumIdle与maximumPoolSize一样，为了性能考虑，不建议设置此值
        //properties.setProperty("minimumIdle", "8");
        return properties;
    }

}
