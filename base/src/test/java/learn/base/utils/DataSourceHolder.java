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
 * @author Zephyr
 * @date 2021/4/15.
 */
public class DataSourceHolder implements Closeable {

    private HikariDataSource dataSource;
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
    private List<Connection> connectionList = new ArrayList<>();
    private final AtomicBoolean isShutdown = new AtomicBoolean();

    private DataSourceHolder() {
    }

    public static DataSourceHolder hold(HikariDataSource dataSource) {
        assert dataSource != null && dataSource.isRunning();
        DataSourceHolder dataSourceHolder = new DataSourceHolder();
        dataSourceHolder.dataSource = dataSource;
        return dataSourceHolder;
    }

    public synchronized Connection getConnection() {
        if (isShutdown.get()) {
            throw new IllegalStateException("DataSource in " + DataSourceHolder.class.getSimpleName() + " already closed！");
        }
        Connection connection = connectionHolder.get();
        if (connection == null) {
            Connection newConn;
            try {
                newConn = dataSource.getConnection();
                connectionHolder.set(newConn);
                connectionList.add(newConn);
                return newConn;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        } else {
            return connection;
        }
    }

    @Override
    public void close() throws IOException {
        if (isShutdown.getAndSet(true)) {
            connectionList.forEach(conn -> {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            dataSource.close();
        }
    }
}
