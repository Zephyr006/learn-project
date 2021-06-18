package learn.base.utils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.Connection;

/**
 * @author Zephyr
 * @date 2021/5/6.
 */
public interface ConnectionHolder extends Closeable {

    Connection getConnection();

    DataSource getDataSource();
}
