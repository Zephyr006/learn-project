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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2021/3/30.
 */
public class RelationTest {
    private static final String driverClassName = "com.mysql.jdbc.Driver";
    private static final String url_template = "jdbc:mysql://%s/%s?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8&useSSL=false";

    // @Test
    public void selectRelation() throws SQLException {
        String userSql = "select id from user.user order by id desc limit 1";

        String questionIdSql = "select question_id from question.question_group where group_id in (2936, 236, 133)";

        String userQuestionSql = "SELECT user_id,count(*) from user_question_%s\n" +
                "WHERE question_id in ( %s )\n" +
                "and user_id >= %s\n" +
                "GROUP BY user_id HAVING COUNT(*) > 13 limit %s";


        try (final HikariDataSource dataSource = initDataSource()) {
            long nowTime = System.currentTimeMillis();
            StopWatch stopWatch = StopWatch.createAndStart("查询指定做题集下满足指定正确率的用户id");
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            try (final Connection connection = dataSource.getConnection()) {
                System.out.println(connection.isValid(3));
                List<Long> userIds = new ArrayList<>();
                List<Long> questionIds = new ArrayList<>();

                try (final Statement statement = connection.createStatement()) {
                    final ResultSet resultSet = statement.executeQuery(userSql);
                    long userIdOffset = 20_0000;
                    while (resultSet.next()) {
                        userIds.add(resultSet.getLong(1) - userIdOffset);
                    }
                    System.out.println("userIds.size = " + userIds.size());

                    final ResultSet resultSet1 = statement.executeQuery(questionIdSql);
                    while (resultSet1.next()) {
                        questionIds.add(resultSet1.getLong(1));
                    }
                    System.out.println("questionIds.size = " + questionIds.size());

                    // query ----------------
                    List<Future<List<Long>>> futureList = new ArrayList<>();
                    final String qStr = StringUtils.join(questionIds, ",");
                    for (int i = 0; i < 100; i++) {
                        long finalI = i;
                        futureList.add(executorService.submit(() -> queryQuestion(finalI, userIds, qStr, userQuestionSql, dataSource)));
                    }
                    //final Map<Long, List<Long>> userIdMap = userIds.stream().collect(Collectors.groupingBy(id -> id % 100));
                    //userIdMap.forEach((key, value) -> futureList.add(executorService.submit(() ->
                    //        queryQuestion(key, value, qStr, userQuestionSql, dataSource)
                    //)));

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
                    System.out.println(list);
                }
            }
        }
    }


    private List<Long> queryQuestion(Long key, List<Long> userIds, String questionIds, String sql, HikariDataSource dataSource) {
        final String querySql = String.format(sql, key, questionIds, userIds.get(0), 1000);
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement();
                 final ResultSet resultSet = statement.executeQuery(querySql)) {
                List<Long> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(resultSet.getLong(1));
                }
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


    HikariDataSource initDataSource() {
        Properties props = HikariConfigUtil.initDefaultProps();
        return new HikariDataSource(new HikariConfig(props));
    }
}

