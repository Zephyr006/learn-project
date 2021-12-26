package learn.base.test.business;

import com.zaxxer.hikari.HikariDataSource;
import learn.base.test.business.entity.Statistics;
import learn.base.test.business.entity.TagOrKnowledge;
import learn.base.test.business.entity.UserDataConfig;
import learn.base.test.business.entity.UserQuestionLog;
import learn.base.utils.HikariConfigUtil;
import learn.base.utils.ResultSetUtils;
import learn.base.utils.StopWatch;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import static learn.base.test.business.entity.UserQuestion.UserQuestionSummary;

/**
 * map_questiontype - 客观：1、2、4 、5    ；主观：3
 * @author Zephyr
 * @date 2021/4/7.
 */
public class UserStatV3 extends BaseUserStat {

    public static void main(String[] args) {
        StopWatch stopWatch = StopWatch.createAndStart("学员做题情况统计");
        final Map<Long, List<Long>> partitionToUserIdMap = allUserIds.stream()
                .collect(Collectors.groupingBy(userId -> userId % 1000));
        System.out.println("开始并发查询用户相关做题记录，并发线程数 = " + parallelism + "，学员总数 = " + allUserIds.size());
        allUserIds.clear();


        List<Statistics> statistics = mainProcess(partitionToUserIdMap);
        if (CollectionUtils.isNotEmpty(statistics)) {
            outputToExcel(statistics, Statistics::getLevel, config.getExcelSavePath());
        } else {
            System.err.println("Error：没有统计数据可供输出！");
        }
        System.out.println(stopWatch.stopAndPrint());
    }

    private static List<Statistics> mainProcess(final Map<Long, List<Long>> partitionToUserIdMap) {
        try (HikariDataSource dataSource = new HikariDataSource(HikariConfigUtil.buildHikariConfig(
                config.dbHostAndUsername().getLeft(), UserDataConfig.dbName,
                config.dbHostAndUsername().getRight(), UserDataConfig.password))){
            Connection connection = dataSource.getConnection();
            System.out.println("connection is valid ? " + connection.isValid(2));


            //做题次数
            final int times = 1;
            TagOrKnowledge.TreeNode treeRoot = new TagOrKnowledge.TreeNode(config.getTreeRoot().getRight(), config.getTreeRoot().getLeft(), null);
            List<TagOrKnowledge> needQueryTreeNodeList = getNeedQueryTreeNodes(treeRoot, connection);
            if (needQueryTreeNodeList.isEmpty()) {
                System.err.println("要查询的节点个数为0！！！");
                return Collections.emptyList();
            }
            System.out.println("根节点名称【" + config.getTreeRoot().getRight() + "】，要查询的目标节点个数为 " + needQueryTreeNodeList.size());


            // 2. 查询标签下的题目question
            List<Long> allTagIds = needQueryTreeNodeList.stream().map(TagOrKnowledge::getId).collect(Collectors.toList());
            Set<Long> allQuestionIds;
            if (config.isForTag()) {
                allQuestionIds = getAllQuestionIdByTagId(connection, allTagIds);
            } else {
                allQuestionIds = getAllQuestionIdByKnowledgeId(connection, allTagIds);
            }
            System.out.println("查询完所有标签及题目元数据，总题目数 = " + allQuestionIds.size());

            CountDownLatch countDownLatch = new CountDownLatch(partitionToUserIdMap.size());
            String questionIdStr = StringUtils.join(allQuestionIds, ",");
            final String userQuestionLogSql = "select user_id,question_id,submit_id,correct,cost_time from user_question_log_%s " +
                    "where user_id in (%s) and question_id in (" + questionIdStr + ") and cost_time > 900 and status = 1";
            final String submitLogSql = "select id,user_id,scenes_key from submit_log_%s where id in (%s)";
            allQuestionIds.clear();

            final List<UserQuestionSummary> userQuestionSummaryList = new ArrayList<>();
            for (Map.Entry<Long, List<Long>> entry : partitionToUserIdMap.entrySet()) {

                Runnable task = () -> {
                    try (Connection newConn = dataSource.getConnection()){
                        //long startTime = System.currentTimeMillis();
                        // 1. 查出对应学员的所有做题记录，过滤得到每道题的第n次做题数据
                        Map<Long, List<UserQuestionLog>> needQueryQuestionLogsMap = Collections.emptyMap();
                        try (Statement statement = newConn.createStatement();) {
                            ResultSet resultSet = statement.executeQuery(
                                    String.format(userQuestionLogSql, entry.getKey(), StringUtils.join(entry.getValue(), ",")));
                            Collection<UserQuestionLog> userQuestionLogs = ResultSetUtils.parseCollection(resultSet, new ArrayList<>(), UserQuestionLog.class);
                            Map<Long, List<UserQuestionLog>> userToAllQuestionLogsMap = Optional.ofNullable(userQuestionLogs).orElse(Collections.emptyList())
                                    .stream().collect(Collectors.groupingBy(UserQuestionLog::getUserId));
                            // 按用户分区,只取"对应次数"的答题记录
                            needQueryQuestionLogsMap = userToAllQuestionLogsMap.values().stream()
                                    .map(questionLogs -> questionLogs.stream().collect(Collectors.groupingBy(UserQuestionLog::getQuestionId)))
                                    .flatMap(map -> map.values().stream())
                                    .filter(questionLogs -> questionLogs.size() >= times)
                                    .map(questionLogs -> questionLogs.get(times - 1))
                                    .collect(Collectors.groupingBy(UserQuestionLog::getUserId));
                            // 过滤没有做够50道题的用户
                            needQueryQuestionLogsMap.entrySet().removeIf(e -> e.getValue().size() < config.getDoQuestionSize());
                            userQuestionLogs.clear();
                        }
                        if (MapUtils.isEmpty(needQueryQuestionLogsMap)) {
                            return;
                        }

                        // 2. 通过submit_log表查出每道题对应的场景信息（*前面已经根据userId分区，这里查询submit_log表的分区值始终能与前面的分区对应！！！*）
                        Set<Long> submitLogIds = needQueryQuestionLogsMap.values().stream()
                                .flatMap(Collection::stream).map(UserQuestionLog::getSubmitId).collect(Collectors.toSet());
                        ResultSet resultSet233 = newConn.createStatement().executeQuery(
                                String.format(submitLogSql, entry.getKey() % 100, StringUtils.join(submitLogIds, ",")));
                        List<UserQuestionLog.SubmitLog> submitLogs = (List<UserQuestionLog.SubmitLog>) ResultSetUtils.parseCollection(
                                resultSet233, new ArrayList<>(submitLogIds.size()), UserQuestionLog.SubmitLog.class);
                        // 填充每条答题记录的sceneKey
                        Map<Long, Integer> submitIdToScenesKeyMap = submitLogs.stream().collect(Collectors
                                .toMap(UserQuestionLog.SubmitLog::getId, UserQuestionLog.SubmitLog::getScenesKey, (l, r) -> l));
                        needQueryQuestionLogsMap.forEach((userId, questionLogs) ->
                                questionLogs.forEach(questionLog -> questionLog.setScenesKey(submitIdToScenesKeyMap.get(questionLog.getSubmitId()))));

                        // 3. 计算这批学员的正确率和平均速度
                        List<UserQuestionSummary> summaryList = needQueryQuestionLogsMap.entrySet().stream()
                                .map(e -> buildUserQuestionSummaryByQuestionLogs(e.getKey(), e.getValue())).collect(Collectors.toList());
                        if (!summaryList.isEmpty()) {
                            userQuestionSummaryList.addAll(summaryList);
                        }
                        System.out.println("执行完一个 question_log分区子任务，分区标识 " + entry.getKey() + " ，人数 "+ summaryList.size());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                };
                executorService.submit(task);
            }
            countDownLatch.await();


            List<Statistics> statisticsList = new ArrayList<>();
            if (userQuestionSummaryList.size() >= 60) {
                System.out.println(String.format("满足题目覆盖率要求的用户 %d 人", userQuestionSummaryList.size()));
                // 5. 按用户正确率排列，计算每档的平均正确率和平均做题速度；
                userQuestionSummaryList.removeIf(element ->
                        Double.compare(element.getAverageCorrectRate(), 1d) == 0 );
                userQuestionSummaryList.sort(Comparator.reverseOrder());

                // 按人数分档
                List<Statistics> byUserCount = countByUserQuantity(userQuestionSummaryList, config.getTreeRoot().getRight());
                statisticsList.addAll(byUserCount);

            } else {
                System.err.println("节点【" + config.getTreeRoot().getRight() + "】满足题目覆盖率的人数过少，无法生成分档的做题统计数据");
            }
            Pair<Double, Integer> correctRateAndSpeed = getSummaryCorrectRateAndSpeed(userQuestionSummaryList);
            statisticsList.add(Statistics.builder().name(config.getTreeRoot().getRight() + " 统计").level(0)
                    .correctRate(correctRateAndSpeed.getLeft()).speed(correctRateAndSpeed.getRight()).count(userQuestionSummaryList.size()).build());
            return statisticsList;
        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            executorService.shutdown();
        }
    }




}
