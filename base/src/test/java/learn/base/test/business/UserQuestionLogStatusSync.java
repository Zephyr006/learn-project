package learn.base.test.business;

import com.mysql.cj.jdbc.result.ResultSetImpl;
import com.mysql.cj.protocol.ResultsetRows;
import com.zaxxer.hikari.HikariDataSource;
import learn.base.test.business.entity.UserDataConfig;
import learn.base.utils.ConnectionHolder;
import learn.base.utils.HikariConfigUtil;
import learn.base.utils.ThreadLocalConnectionHolder;
import learn.example.javase.SleepUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Zephyr
 * @since 2021-5/8.
 */
public class UserQuestionLogStatusSync {
    static String url = "jdbc:mysql://%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true&useCursorFetch=true";
    static ExecutorService executorService = Executors.newFixedThreadPool(5);
    static ConcurrentMap<Integer, List<Long>> illegalLogMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws ClassNotFoundException {
        String username = UserDataConfig.JK_DbHostAndUsername.getRight();
        String password = UserDataConfig.password;

        String submitLogSql =
        "select id from submit_log_%s where status = 0";
        String selectUserQuestionLog = "select id from user_question_log_%s where submit_id in (%s) and status = 1";
        String userQuestionLogSql = "update user_question_log_%s set status = 0 where submit_id in (%s) and status = 1";


        //Connection connection = DriverManager.getConnection(String
        //        .format(url, UserDataConfig.JK_DbHostAndUsername.getLeft(), UserDataConfig.dbName), username, password)
        // 1. 加载驱动类
        Class.forName("com.mysql.cj.jdbc.Driver");
        // 2. 建立连接
        try (ConnectionHolder dataSource = ThreadLocalConnectionHolder.hold(new HikariDataSource(HikariConfigUtil.buildHikariConfig(
                UserDataConfig.JK_DbHostAndUsername.getLeft(), UserDataConfig.dbName,
                UserDataConfig.JK_DbHostAndUsername.getRight(), UserDataConfig.password)));
                Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(2)) {
                System.err.println("连接无效或为只读连接，退出......");
                return;
            }

            String sql = String.format(submitLogSql, 0) + " and id < 200000";
            List<Long> test = new ArrayList<>();
            try (Statement statement = connection.createStatement()){
                statement.setFetchSize(1_0000);
                try (ResultSet submitLogResult = statement.executeQuery(sql)) {
                    while (submitLogResult.next()) {
                        test.add(submitLogResult.getLong(1));
                    }
                }
            }

            System.out.println(test);
            test.clear();
            try (PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
                statement.setFetchSize(1_0000);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    test.add(resultSet.getLong(1));
                }
            }


            System.out.println(test);
            System.exit(0);


            String strSubmitLogIds;
            for (int i = 0; i < 100; i++) {
                // 1. 查询出当前submit_log表中所有 status=0 的记录id
                List<Long> submitLogIds = new ArrayList<>(4096);
                long startTime = System.currentTimeMillis();
                try (Statement statement = connection.createStatement()) {
                    statement.setFetchSize(1_0000);
                    String sqlTemp = String.format(submitLogSql, i);
                    try (ResultSet submitLogResult = statement.executeQuery(sqlTemp + " and id < 2200000")) {
                        while (submitLogResult.next()) {
                            submitLogIds.add(submitLogResult.getLong(1));
                        }
                    }
                    System.err.println(i + " - submitLogIds.size = " + submitLogIds.size());
                    strSubmitLogIds = join(submitLogIds, ",");
                    submitLogIds.clear();
                    updateUserQuestionLog(selectUserQuestionLog, dataSource, strSubmitLogIds, i);


                    try (ResultSet submitLogResult = statement.executeQuery(sqlTemp + " and id >= 2200000")) {
                        while (submitLogResult.next()) {
                            submitLogIds.add(submitLogResult.getLong(1));
                        }
                    }
                    System.err.println(i + " - submitLogIds.size = " + submitLogIds.size());
                    strSubmitLogIds = join(submitLogIds, ",");
                    submitLogIds.clear();
                    updateUserQuestionLog(selectUserQuestionLog, dataSource, strSubmitLogIds, i);
                }


                System.out.println("submitLog partition cost time = " + (System.currentTimeMillis() - startTime));
                strSubmitLogIds = null;
                System.gc();
                SleepUtil.sleep(900);
            }

            System.out.println(illegalLogMap);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateUserQuestionLog(final String selectUserQuestionLog, final ConnectionHolder dataSource, final String strSubmitLogIds, final int i) {
        for (int j = 0; j < 10; j++) {
            final int partition = i + j * 100;
            Runnable task = () -> {
                long nowTime = System.currentTimeMillis();
                List<Long> questionLogIds = new ArrayList<>();
                Connection connection = dataSource.getConnection();
                try (Statement statement = connection.createStatement()) {
                    statement.setFetchSize(1_000);
                    //statement.executeUpdate(String.format(userQuestionLogSql, partition, strSubmitLogIds));
                    try (ResultSet resultSet = statement.executeQuery(String.format(selectUserQuestionLog, partition, strSubmitLogIds))) {
                        while (resultSet.next()) {
                            questionLogIds.add(resultSet.getLong(1));
                        }
                    }
                    System.out.println("question log partition cost time = " + (System.currentTimeMillis() - nowTime));
                    if (!questionLogIds.isEmpty()) {
                        System.err.println(StringUtils.join(questionLogIds));
                        illegalLogMap.compute(partition, (k, v) -> {
                            if (v == null) {
                                List<Long> ids = new ArrayList<>(questionLogIds.size() * 2);
                                ids.addAll(questionLogIds);
                                return ids;
                            } else {
                                v.addAll(questionLogIds);
                                return v;
                            }
                        });
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            };
            executorService.submit(task);
        }
    }

    // @Test
    public void testJdbc() throws SQLException, ClassNotFoundException {
        List<Long> list = Arrays.asList(4L, 6L, 8L, 9L, 10L);
        String host = "39.106.73.19:3306";
        String dbName = UserDataConfig.dbName;
        String username = "root";
        String password = UserDataConfig.password;
        // 1. 加载驱动类
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection(String
                .format(url, host, dbName), username, password);
        boolean valid = connection.isValid(1);

        boolean result = connection.createStatement().execute("/* ping */");
        System.exit(0);

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            buf.append("?,");
        }
        String submitLogSql = "select id from submit_log_%s where status = 0 and id in (" + buf.deleteCharAt(buf.length()-1).toString() + ") and id < 1000";
        PreparedStatement statement = connection.prepareStatement(String.format(submitLogSql, 0), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        statement.setFetchSize(2);
        for (int i = 0; i < list.size(); ) {
            statement.setObject(++i, list.get(i - 1));
        }

        ResultSetImpl resultSet = (ResultSetImpl) statement.executeQuery();
        ResultsetRows rows = resultSet.getRows();
        while (resultSet.next()) {
            System.out.println(resultSet.getLong(1));
        }
    }

    @Test
    public void ss2() {
        int partition = 1;
        List<Long> questionLogIds = Arrays.asList(1L);
        illegalLogMap.compute(partition, (k, v) -> {
            if (v == null) {
                List<Long> ids = new ArrayList<>(questionLogIds.size() * 2);
                ids.addAll(questionLogIds);
                return ids;
            }
            v.addAll(questionLogIds);
            return v;
        });
        List<Long> questionLogIds2 = Arrays.asList(12L);
        illegalLogMap.compute(partition, (k, v) -> {
            if (v == null) {
                List<Long> ids = new ArrayList<>(questionLogIds2.size() * 2);
                ids.addAll(questionLogIds2);
                return ids;
            }
            v.addAll(questionLogIds2);
            return v;
        });
        System.out.println(illegalLogMap);
    }


    private static String join(Collection<Long> collection, String separator) {
        if (CollectionUtils.isEmpty(collection)) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(separator);
        collection.forEach(n -> joiner.add(String.valueOf(n)));
        return joiner.toString();
    }
}
