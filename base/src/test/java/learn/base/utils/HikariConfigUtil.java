package learn.base.utils;

import com.zaxxer.hikari.HikariConfig;

import java.util.Properties;

/**
 *
 * 使用游标分批获取查询的数据的条件 {@see com.mysql.cj.jdbc.StatementImpl#useServerFetch()}：其中useCursorFetch和FetchSize参数值需要手动设置
 * 游标查询的效果：查询时会强制使用PreparedStatement，发送查询语句数据包时会在数据包头包含FetchSize的值，数据库最多返回FetchSize条结果值
 *              数据库返回的查询结果会缓存在 ResultSet.ResultsetRows.rows，形式为一个list
 *              当消费完当前已返回的结果后，会调用{@see com.mysql.cj.protocol.a.result.ResultsetRowsCursor#hasNext()}查询下一批数据
 *
 *
 * @author Zephyr
 * @date 2021/4/8.
 */
public class HikariConfigUtil {


    public static Properties initDefaultProps() {
        String host = "192.168.2.136";
        String dbName = "relation";
        String username = "root";
        String password = "root";
        return HikariConfigUtil.initProps(host, dbName, username, password);
    }

    public static HikariConfig buildHikariConfig(String host, String dbName, String username, String password) {
        return new HikariConfig(initProps(host, dbName, username, password));
    }

    /**
     * 用于Hikari数据库连接池的参数初始化（Properties类的参数格式为驼峰格式，spring配置文件为下划线格式）
     * 最终会调用HikariConfig类中的 setXxx 方法设值（如setPassword），不存在对应方法则抛异常
     */
    public static Properties initProps(String host, String dbName, String username, String password) {
        Properties properties = new Properties();
        String url_template = "jdbc:mysql://%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true&useCursorFetch=true";

        properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("jdbcUrl", String.format(url_template, host, dbName));
        properties.setProperty("username", username);
        properties.setProperty("password", password);
        // 连接只读数据库时配置为true， 保证安全
        properties.setProperty("readOnly", "true");
        // 是否自动提交事务
        properties.setProperty("autoCommit", "false");
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
        properties.setProperty("maximumPoolSize", "101");
        // 连接池空闲连接的最小数量。默认minimumIdle与maximumPoolSize一样，为了性能考虑，不建议设置此值
        //properties.setProperty("minimumIdle", "8");
        return properties;
    }

}
