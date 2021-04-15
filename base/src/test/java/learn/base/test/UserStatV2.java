package learn.base.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import learn.base.test.entity.Tag;
import learn.base.test.entity.UserDataConfig;
import learn.base.test.entity.UserQuestion;
import learn.base.utils.DataSourceHolder;
import learn.base.utils.ExcelUtil;
import learn.base.utils.HikariConfigUtil;
import learn.base.utils.StopWatch;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

import static learn.base.test.entity.UserQuestion.Statistics;
import static learn.base.test.entity.UserQuestion.UserQuestionSummary;
import static learn.base.test.entity.UserQuestion.userQuestionSql;

/**
 * @author Zephyr
 * @date 2021/4/7.
 */
public class UserStatV2 {

    static UserDataConfig config = new UserDataConfig.JkSearch6();
    static int parallelism = 25;
    static ExecutorService executorService = Executors.newFixedThreadPool(parallelism);


    public static void main(String[] args) throws InterruptedException {
        StopWatch stopWatch = StopWatch.createAndStart("学员做题情况统计");
        List<Long> allUserIds = UserStat.readFromExcel(config.getExcelPath());
        Map<Long, List<Long>> partitionToUserIdMap = allUserIds.stream().collect(Collectors.groupingBy(userId -> userId % 100));
        System.out.println("开始并发查询用户相关做题记录，并发线程数 = " + parallelism);


        String dbName = "relation";
        String password = "";
        try {
            HikariConfig hikariConfig = new HikariConfig(HikariConfigUtil.initProps(
                    config.dbHostAndUsername().getLeft(), dbName, config.dbHostAndUsername().getRight(), password));
            DataSourceHolder dataSourceHolder = DataSourceHolder.hold(new HikariDataSource(hikariConfig));


            Connection mainConnection = dataSourceHolder.getConnection();
            System.out.println("\nDB connected ? " + mainConnection.isValid(4));

            // 1 以tagTreeId做查询条件查询标签
            Tag.TagTreeNode treeRoot = new Tag.TagTreeNode(config.getTreeRoot().getRight(), config.getTreeRoot().getLeft(), null);
            List<Tag.TagTreeNode> allLeafTag = UserStatV2.getTagTreeNodes(treeRoot, mainConnection);


            if (allLeafTag.isEmpty()) {
                System.err.println("叶子节点个数为0！！！");
                return;
            }
            System.out.println("叶子节点个数为 " + allLeafTag.size());
            // 2. 查询标签下的题目question
            List<Callable<Pair<Tag, Set<Long>>>> questionIdCallableList = allLeafTag.stream().map(tag -> (Callable<Pair<Tag, Set<Long>>>) () -> {
                    Set<Long> allQuestionIds;
                Connection connection = dataSourceHolder.getConnection();
                if (config.isForTag()) {
                    allQuestionIds = UserStatV2.getAllQuestionIdByTagId(connection, Collections.singletonList(tag.getId()));
                } else {
                    allQuestionIds = UserStatV2.getAllQuestionIdByKnowledgeId(connection, Collections.singletonList(tag.getId()));
                }
                return new ImmutablePair<>(tag, allQuestionIds);
            }).collect(Collectors.toList());

            List<Future<Pair<Tag, Set<Long>>>> futureList = executorService.invokeAll(questionIdCallableList);
            System.out.println("查询完所有标签及题目元数据 " + stopWatch.prettyPrint());
            List<Pair<Tag, Set<Long>>> tagToQuestionIdList = futureList.stream().map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());


            List<Statistics> resultList = new ArrayList<>();
            for (Pair<Tag, Set<Long>> tagAndQuestionIds : tagToQuestionIdList) {
                if (tagAndQuestionIds == null) {
                    System.err.println("跳过了节点的做题情况统计，因为其题目查询出现了异常");
                    continue;
                }
                if (tagAndQuestionIds.getRight().size() < UserDataConfig.doQuestionSize) {
                    System.err.println(String.format("跳过了节点【%s】的做题情况统计，因为其题目题目数量过少 (%d)",
                            tagAndQuestionIds.getKey().getName(), tagAndQuestionIds.getRight().size()));
                    continue;
                }

                Tag tag = tagAndQuestionIds.getLeft();
                Map<Long, List<UserQuestion>> userIdToQuestionsMap = UserStatV2
                        .getUserIdToQuestionsMap(dataSourceHolder, tagAndQuestionIds.getRight(), partitionToUserIdMap);

                if (userIdToQuestionsMap.size() >= 60) {
                    System.out.println(String.format("节点【%s】：样本总数 %d 人，其中满足题目覆盖率要求的用户 %d 人",
                            tag.getName(), allUserIds.size(), userIdToQuestionsMap.size()));
                    // 5. 按用户正确率排列，计算每档的平均正确率和平均做题速度；
                    List<UserQuestionSummary> userSummaryList = userIdToQuestionsMap.values().stream()
                            .map(UserQuestionSummary::new)
                            .sorted(Comparator.reverseOrder())
                            .collect(Collectors.toList());

                    // 按人数分档
                    List<Statistics> byUserCount = UserStatV2.countByUserQuantity(userSummaryList, tag.getName());
                    //byUserCount.forEach(s -> System.out.println(s.toFormatString()));
                    resultList.addAll(byUserCount);
                    Pair<Double, Integer> correctRateAndSpeed = UserStatV2.getCorrectRateAndSpeed(userSummaryList);

                    resultList.add(new Statistics(tag.getName() + " 统计", 0,
                            correctRateAndSpeed.getLeft(), correctRateAndSpeed.getRight(), userIdToQuestionsMap.size()));
                    //outputToExcel(byUserCount, Statistics::getLevel, excelSavePath);
                } else {
                    System.err.println("节点【" + tag.getName() + "】满足题目覆盖率的人数过少，无法生成做题统计数据");

                    List<UserQuestionSummary> questionSummaryList = userIdToQuestionsMap.values().stream()
                            .map(UserQuestionSummary::new).collect(Collectors.toList());
                    Pair<Double, Integer> correctRateAndSpeed = getCorrectRateAndSpeed(questionSummaryList);
                    resultList.add(new Statistics(tag.getName(), 0,
                            correctRateAndSpeed.getLeft(), correctRateAndSpeed.getRight(), userIdToQuestionsMap.size()));
                }

            }

            outputToExcel(resultList, Statistics::getLevel, config.getExcelSavePath());


            dataSourceHolder.close();
            executorService.shutdown();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        System.out.println(stopWatch.stopAndPrint());
    }

    private static List<Tag.TagTreeNode> getTagTreeNodes(final Tag.TagTreeNode treeRoot, final Connection connection) throws SQLException {
        List<Tag> allTags;
        boolean isTreeId = config.isTreeId();
        boolean isForTag = config.isForTag();
        if (isForTag && isTreeId) {
            allTags = UserStatV2.getAllTagByTreeId(config.getTreeRoot().getLeft(), connection);
        }
        else if (!isForTag && isTreeId) {
            allTags = UserStatV2.getAllKnowledgeByTreeId(config.getTreeRoot().getLeft(), connection);
        }
         else if (isForTag && !isTreeId){
            allTags = UserStatV2.getAllTagByParentId(config.getTreeRoot().getLeft(), connection);
        } else {
            allTags = Collections.emptyList();
        }

        if (isTreeId) {
            List<Long> parentTagIds = allTags.stream()
                    .filter(tag -> 0 == tag.getParentId())
                    .map(tag -> {
                        treeRoot.getChildNodes().add(new Tag.TagTreeNode(tag.getName(), tag.getId(), tag.getParentId()));
                        return tag.getId();
                    }).collect(Collectors.toList());
            UserStatV2.parseTagTree(treeRoot, allTags, parentTagIds);
        } else {
            UserStatV2.parseTagTree(treeRoot, allTags, Collections.singletonList(treeRoot.getId()));
        }
        return Tag.TagTreeNode.getAllLeafTag(treeRoot);
    }


    /**
     * 返回正确率（left）和答题速度（right）
     */
    private static Pair<Double, Integer> getCorrectRateAndSpeed(List<UserQuestionSummary> summaryList) {
        if (summaryList.isEmpty()) {
            return new ImmutablePair<>(-1d, -1);
        }
        boolean getLowest = true;
        if (getLowest) {
            int size = summaryList.size();
            return new ImmutablePair<>(summaryList.get(size - 1).getAverageCorrectRate(), summaryList.get(size - 1).getAverageSpeed());
        // average
        } else {
            double averageCorrectRate = summaryList.stream()
                    .mapToDouble(UserQuestionSummary::getAverageCorrectRate).average().orElse(-1d);
            double averageSpeed = summaryList.stream()
                    .mapToInt(UserQuestionSummary::getAverageSpeed).average().orElse(-1d);
            return new ImmutablePair<>(averageCorrectRate, (int) averageSpeed);
        }
    }

    private static void outputToExcel(List<Statistics> statistics, Function<Statistics, Object> levelGetter,  String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
            ExcelUtil.export(statistics,
                    Arrays.asList("标签名称", "档位", "正确率", "答题速度(秒)", "人数"),
                    Arrays.asList(Statistics::getName, levelGetter, Statistics::getCorrectRateDesc, Statistics::getSpeedSecond, Statistics::getCount),
                    outputStream);
        } catch (IOException e) {
            System.err.println("Excel export error");
            e.printStackTrace();
        }
    }

    /**
     * 删除所有不满足做题正确率要求的用户
     */
    private static Map<Long, List<UserQuestion>> removeAllIllegalUser(
            int threshold, Map<Long, List<UserQuestion>> userIdToQuestionsMap) {
        userIdToQuestionsMap.entrySet().removeIf(longListEntry -> longListEntry.getValue().size() < threshold);
        return userIdToQuestionsMap;
    }

    /**
     * 按人数分档，输出统计结果
     */
    private static List<Statistics> countByUserQuantity(List<UserQuestionSummary> userSummaryList, String tagName) {
        int currentIndex = 0;
        int totalUserCount = userSummaryList.size();
        int levelSize = totalUserCount * config.separate / 100;
        int remainder = levelSize % totalUserCount;
        levelSize = (remainder > 6 && totalUserCount > (200 / config.separate))  ? levelSize + 1 : levelSize;

        ArrayList<Statistics> statisticsList = new ArrayList<>(21);
        while (currentIndex < totalUserCount) {
            int toIndex = Math.min(currentIndex + levelSize, totalUserCount);
            List<UserQuestionSummary> subList = userSummaryList.subList(currentIndex, toIndex);

            Pair<Double, Integer> correctRateAndSpeed = getCorrectRateAndSpeed(subList);
            //currentIndex = toIndex;
            if ((currentIndex = toIndex) % levelSize > 0) {
                int level = (currentIndex) / levelSize + 1;
                statisticsList.add(new Statistics(tagName, level, correctRateAndSpeed.getLeft(), correctRateAndSpeed.getRight(), subList.size()));
                //consoleOutput(level, averageCorrectRate, (int) averageSpeed, subList.size());
            } else {
                statisticsList.add(new Statistics(tagName, currentIndex / levelSize, correctRateAndSpeed.getLeft(), correctRateAndSpeed.getRight(), subList.size()));
            }
        }
        return statisticsList;
        //final int totalLevel = statisticsList.size();
        //return statisticsList.stream()
        //        .peek(stat -> stat.setLevel(totalLevel - stat.getLevel() + 1))
        //        .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    private static void parseTagTree(final Tag.TagTreeNode tagTreeRoot, final List<Tag> allTags, List<Long> parentTagIds) {
        List<Long> finalParentTagIds = parentTagIds;
        allTags.removeIf(tag -> finalParentTagIds.contains(tag.getId()));
        while (!allTags.isEmpty()) {
            List<Long> finalParentTagIds1 = parentTagIds;
            parentTagIds = allTags.stream()
                    .filter(tag -> finalParentTagIds1.contains(tag.getParentId()))
                    .map(Tag::getId).collect(Collectors.toList());
            tagTreeRoot.grow(allTags);
            List<Long> finalParentTagIds2 = parentTagIds;
            allTags.removeIf(tag -> finalParentTagIds2.contains(tag.getId()));
        }
    }

    private static List<Tag> getAllTagByTreeId(final long treeId, final Connection connection) throws SQLException {
        List<Tag> allTags = new ArrayList<>();
        ResultSet tagResultSet = connection.createStatement().executeQuery(
                String.format("select id,parentId,name from `public-admin`.`questiontag` where treeId = %s and status = 0 and type = 1", treeId));
        while (tagResultSet.next()) {
            allTags.add(new Tag(tagResultSet.getLong(1), tagResultSet.getLong(2), tagResultSet.getString(3)));
        }
        return allTags;
    }

    private static List<Tag> getAllTagByParentId(final long treeId, final Connection connection) throws SQLException {
        List<Tag> subList = new ArrayList<>();
        ResultSet tagResultSet = connection.createStatement().executeQuery(
                String.format("select id,parentId,name from `public-admin`.`questiontag` where id = %s and status = 0 and type = 1", treeId));
        while (tagResultSet.next()) {
            subList.add(new Tag(tagResultSet.getLong(1), tagResultSet.getLong(2), tagResultSet.getString(3)));
        }
        List<Tag> allTags = new ArrayList<>(subList);
        while (!subList.isEmpty()) {
            List<Long> nextParentIds = subList.stream().map(Tag::getId).collect(Collectors.toList());
            ResultSet resultSet = connection.createStatement().executeQuery(String.format(
                    "select id,parentId,name from `public-admin`.`questiontag` where parentId in (%s) and status = 0 and type = 1",
                    StringUtils.join(nextParentIds, ",")));
            subList.clear();
            while (resultSet.next()) {
                subList.add(new Tag(resultSet.getLong(1), resultSet.getLong(2), resultSet.getString(3)));
            }
            if (!subList.isEmpty()) {
                allTags.addAll(subList);
            }
        }
        return allTags;
    }

    private static List<Tag> getAllKnowledgeByTreeId(final long treeId, final Connection connection) throws SQLException {
        List<Tag> allTags = new ArrayList<>();
        ResultSet tagResultSet = connection.createStatement().executeQuery(
                String.format("select id,parent_id,name from `question`.`knowledge` where root_id = %s and status = 1", treeId));
        while (tagResultSet.next()) {
            allTags.add(new Tag(tagResultSet.getLong(1), tagResultSet.getLong(2), tagResultSet.getString(3)));
        }
        return allTags;
    }

    private static List<Statistics> countByCorrectRate(
            final List<UserQuestionSummary> userSummaryList) {
        MultiValuedMap<Integer, UserQuestionSummary> multiValuedMap = new ArrayListValuedHashMap<>();
        for (UserQuestionSummary userQuestionSummary : userSummaryList) {
            int percentCorrectRate = (int) (userQuestionSummary.getAverageCorrectRate() * 100);
            if (percentCorrectRate == 100) {
                multiValuedMap.put(percentCorrectRate / config.separate - 1, userQuestionSummary);
            } else {
                multiValuedMap.put(percentCorrectRate / config.separate, userQuestionSummary);
            }
        }
        return multiValuedMap.asMap().entrySet().stream().map(entry -> {
            Pair<Double, Integer> correctRateAndSpeed = UserStatV2.getCorrectRateAndSpeed((List<UserQuestionSummary>)entry.getValue());
            return new Statistics("", entry.getKey()+1, correctRateAndSpeed.getLeft(), correctRateAndSpeed.getRight(), entry.getValue().size());
        }).collect(Collectors.toList());
    }

    private static Map<Long, List<UserQuestion>> getUserIdToQuestionsMap(final DataSourceHolder dataSourceHolder,
                    final Set<Long> allQuestionIds, Map<Long, List<Long>> partitionToUserIdMap) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(partitionToUserIdMap.size());
        Map<Long, List<UserQuestion>> userIdToQuestionsMap = new ConcurrentHashMap<>(128);
        String questionIdStr = StringUtils.join(allQuestionIds, ",");

        // 使用executorService.invokeAll()一次提交多个callable并返回结果集 -- invokeAll方法会阻塞直到所有任务都完成
        /*final List<Callable<List<UserQuestion>>> actions = partitionToUserIdMap.entrySet().stream().map(entry ->
                (Callable<List<UserQuestion>>) () -> {
                    List<UserQuestion> userQuestionList = new ArrayList<>(500);
                    try (Connection connection2 = dataSource.getConnection();
                            Statement statement = connection2.createStatement()) {
                        ResultSet resultSet = statement.executeQuery(String.format(
                                userQuestionSql, entry.getKey(), StringUtils.join(entry.getValue(), ","), questionIdStr));
                        while (resultSet.next()) {
                            userQuestionList.add(new UserQuestion(resultSet));
                        }
                        resultSet.close();
                        System.out.println("完成查询做题记录子任务 " + entry.getKey() + ", 题目数量 " + userQuestionList.size());
                        return userQuestionList;
                    }
                }
        ).collect(Collectors.toList());
        // block, and may ignore some exceptions.
        List<Future<List<UserQuestion>>> futures = executorService.invokeAll(actions);
        futures.forEach(future -> {
            try {
                List<UserQuestion> userQuestions = future.get();
                userIdToQuestionsMap.putAll(userQuestions.stream().collect(Collectors.groupingBy(UserQuestion::getUserId)));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });*/

        int minDoQuestionSize = config.doQuestionSize; //(int) (allQuestionIds.size() * doQuestionRate)
        for (Map.Entry<Long, List<Long>> entry : partitionToUserIdMap.entrySet()) {
            CompletableFuture.supplyAsync(() -> {
                List<UserQuestion> userQuestionList = new ArrayList<>();
                String sql = String.format(userQuestionSql,
                        entry.getKey(), StringUtils.join(entry.getValue(), ","), questionIdStr);
                Connection connection = dataSourceHolder.getConnection();
                try (Statement statement = connection.createStatement()) {
                    //final long millis = System.currentTimeMillis();
                    ResultSet resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        userQuestionList.add(new UserQuestion(resultSet));
                    }
                    resultSet.close();
                    //System.out.println("完成查询做题记录子任务 " + entry.getKey() + ", 题目数量 " + userQuestionList.size()
                    //        + " ,耗时（ms） " + (System.currentTimeMillis() - millis));
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println("error sql = " + sql);
                }
                return userQuestionList;
            // 转换 userQuestionList 为 userIdToQuestionsMap 的一部分
            }, executorService).thenAcceptAsync(((userQuestions) -> {
                Map<Long, List<UserQuestion>> subQuestionMap = userQuestions.stream().collect(Collectors.groupingBy(UserQuestion::getUserId));
                subQuestionMap.forEach((key,list) -> {
                    if (list.size() >= minDoQuestionSize) {
                        userIdToQuestionsMap.put(key, list);
                    }
                });
                //userIdToQuestionsMap.putAll(subQuestionMap);
                countDownLatch.countDown();
            }), executorService);
        }
        countDownLatch.await();

        // Blocks until all tasks have completed execution after a shutdown request, instead of CountDownLatch
        //executorService.awaitTermination(6, TimeUnit.MINUTES);
        //System.out.println("所有用户的总做题记录数 = " + userIdToQuestionsMap.values().stream().mapToInt(Collection::size).sum());  // 16_5733
        //System.out.println("所有用户的总做题记录数 = " + userQuestions.size());  // 16_5733
        return userIdToQuestionsMap;
    }

    /**
     * 根据标签id查question
     */
    private static Set<Long> getAllQuestionIdByTagId(final Connection connection, List<Long> allTagIds) throws SQLException {
        String queryQuestionSql = "select id from `public-admin`.`question` where FIND_IN_SET('%s',`tagIds`) and status = 0";
        Set<Long> questionIds = new HashSet<>();
        ResultSet resultSet = connection.createStatement().executeQuery(String.format(queryQuestionSql, allTagIds.get(0)));
        while (resultSet.next()) {
            questionIds.add(resultSet.getLong(1));
        }
        return questionIds;
    }

    /**
     * 根据知识点id查题
     */
    private static Set<Long> getAllQuestionIdByKnowledgeId(final Connection connection, List<Long> allTagIds) throws SQLException {
        ResultSet questionR;
        Set<Long> questionIds = new HashSet<>();
        if (allTagIds.size() == 1) {
            String queryQuestionSql = "select question_id from `question`.`question_knowledge` where knowledge_id = %d and status = 1";
            questionR = connection.createStatement().executeQuery(String.format(queryQuestionSql, allTagIds.get(0)));
        } else {
            String queryQuestionSql = "select question_id from `question`.`question_knowledge` where knowledge_id in (%s) and status = 1";
            questionR = connection.createStatement().executeQuery(String.format(queryQuestionSql, StringUtils.join(allTagIds, ",")));
        }
        while (questionR.next()) {
            questionIds.add(questionR.getLong(1));
        }
        return questionIds;
    }


}
