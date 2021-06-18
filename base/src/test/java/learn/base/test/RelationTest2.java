package learn.base.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import learn.base.utils.HikariConfigUtil;
import learn.base.utils.StopWatch;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2021/3/30.
 */
public class RelationTest2 {
    private static final String driverClassName = "com.mysql.jdbc.Driver";
    private static final String url_template = "jdbc:mysql://%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";
    private static String realSql = null;
    private static ConcurrentMap<Long, Connection> connectionMap = new ConcurrentHashMap<>();

    // @Test
    public void selectRelation() throws SQLException {
        String userSql = "select id from user.user order by id desc limit 1";

        String questionIdSql = "select question_id from question.question_group where group_id in (2936)";

        String userQuestionSql = "SELECT user_id,count(*) from user_question_%s\n" +
                "WHERE question_id in ( %s )\n" +
                "and user_id >= %s\n" +
                "GROUP BY user_id HAVING COUNT(*) > %s limit %s";


        try (final HikariDataSource dataSource = initDataSource()) {
            StopWatch stopWatch = StopWatch.createAndStart("查询指定做题集下满足指定正确率的用户id");
            ExecutorService executorService = Executors.newFixedThreadPool(33);

            try (final Connection connection = dataSource.getConnection()) {
                //List<Long> userIds = new ArrayList<>();
                long userId = 0;
                List<Long> questionIds = new ArrayList<>();

                try (final Statement statement = connection.createStatement()) {
                    final ResultSet resultSet = statement.executeQuery(userSql);
                    long userIdOffset = 100_0000;
                    while (resultSet.next()) {
                        userId = resultSet.getLong(1) - userIdOffset;
                    }
                    System.out.println("userIds.size = " + userId);
                    System.out.println(stopWatch.prettyPrint());

                    final ResultSet questionResultSet = statement.executeQuery(questionIdSql);
                    while (questionResultSet.next()) {
                        questionIds.add(questionResultSet.getLong(1));
                    }
                    System.out.println("questionIds.size = " + questionIds.size());
                    System.out.println(stopWatch.prettyPrint());


                    // query ----------------
                    List<Future<List<Long>>> futureList = new ArrayList<>(100);
                    final String qStr = StringUtils.join(questionIds, ",");
                    for (int i = 0; i < 100; i++) {
                        long finalI = i, finalUserId = userId;
                        //long finalUserId = userId;
                        futureList.add(executorService.submit(() ->
                                queryQuestion(finalI, finalUserId, qStr, questionIds.size(), userQuestionSql, dataSource)));
                    }

                    final List<Long> list = futureList.stream().map(future -> {
                        try {
                            return future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        return Collections.<Long>emptyList();
                    }).flatMap(Collection::stream).limit(1000).collect(Collectors.toList());

                    System.out.println("result.size = " + list.size());
                    System.out.println(stopWatch.stopAndPrint());
                    System.out.println(realSql);
                    System.out.println(list);

                    connectionMap.values().forEach(conn -> {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }


    private List<Long> queryQuestion(Long key, long userId, String questionIds, int questionIdSize, String sql, HikariDataSource dataSource) {
        final String querySql = String.format(sql, key, questionIds, userId, (int)(questionIdSize*0.5), 1000);
        if (realSql == null) {
            realSql = querySql;
        }
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                final ResultSet resultSet = statement.executeQuery(querySql)) {
            List<Long> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(resultSet.getLong(1));
            }
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    HikariDataSource initDataSource() {
        Properties properties = HikariConfigUtil.initDefaultProps();
        return new HikariDataSource(new HikariConfig(properties));
    }
}

