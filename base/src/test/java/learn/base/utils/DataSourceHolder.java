package learn.base.utils;

import com.zaxxer.hikari.HikariDataSource;

import java.io.Closeable;
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
 * @date 2021/4/15.
 */
public class DataSourceHolder implements Closeable {

    private HikariDataSource dataSource;
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private List<Connection> connectionList = new ArrayList<>();
    private final AtomicBoolean isShutdown = new AtomicBoolean();

    private DataSourceHolder() { }

    public static DataSourceHolder hold(HikariDataSource dataSource) {
        assert dataSource != null && dataSource.isRunning();

        DataSourceHolder dataSourceHolder = new DataSourceHolder();
        dataSourceHolder.dataSource = dataSource;
        return dataSourceHolder;
    }

    public Connection getConnection() {
        if (isShutdown.get()) {
            throw new IllegalStateException("DataSource in " + DataSourceHolder.class.getSimpleName() + " already closed！");
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
