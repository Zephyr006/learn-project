package learn.base.utils;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 按照线程持有数据库连接，每个线程持有一个有效的数据库连接
 *
 * @author Zephyr
 * @since 2021-04-15.
 */
public class ThreadLocalConnectionHolder implements ConnectionHolder {

    private HikariDataSource dataSource;
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private List<Connection> connectionList = new ArrayList<>();
    private final AtomicBoolean isShutdown = new AtomicBoolean();

    private ThreadLocalConnectionHolder() { }

    public static ThreadLocalConnectionHolder hold(HikariDataSource dataSource) {
        assert dataSource != null && dataSource.isRunning();

        ThreadLocalConnectionHolder dataSourceHolder = new ThreadLocalConnectionHolder();
        dataSourceHolder.dataSource = dataSource;
        return dataSourceHolder;
    }

    @Override
    public Connection getConnection() {
        if (isShutdown.get()) {
            throw new IllegalStateException("DataSource in " + ThreadLocalConnectionHolder.class.getSimpleName() + " already closed！");
        }
        Connection connection = connectionHolder.get();
        try {
            if (connection == null || connection.isClosed()) {
                synchronized (this) {
                    if (connection == null) {
                        connection = dataSource.getConnection();
                        connectionHolder.set(connection);
                        connectionList.add(connection);
                    } else {
                        // 如果线程原来持有的连接已经关闭，则创建一个新的连接，防止连接被使用者误关闭
                        if (connection.isClosed()) {
                            connectionList.remove(connection);
                            connection = dataSource.getConnection();
                            connectionHolder.set(connection);
                        }
                    }
                }
            }
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() throws IOException {
        if (isShutdown.getAndSet(true)) {
            connectionList.forEach(conn -> {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            if (!dataSource.isClosed()) {
                dataSource.close();
            }
        }
    }
}
