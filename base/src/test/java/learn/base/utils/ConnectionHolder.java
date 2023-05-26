package learn.base.utils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.Connection;

/**
 * @author Zephyr
 * @since 2021-05-06.
 */
public interface ConnectionHolder extends Closeable {

    Connection getConnection();

    DataSource getDataSource();
}
