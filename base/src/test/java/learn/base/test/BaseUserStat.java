package learn.base.test;

import learn.base.test.entity.Statistics;
import learn.base.test.entity.TagOrKnowledge;
import learn.base.test.entity.UserDataConfig;
import learn.base.test.entity.UserQuestion;
import learn.base.test.entity.UserQuestionLog;
import learn.base.utils.ConnectionHolder;
import learn.base.utils.ExcelUtil;
import learn.base.utils.ResultSetUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static learn.base.test.entity.UserQuestion.UserQuestionSummary;
import static learn.base.test.entity.UserQuestion.userQuestionSql;

/**
 * @author Zephyr
 * @date 2021/4/7.
 */
public abstract class BaseUserStat {
    static UserDataConfig config = new UserDataConfig.JkSearch2();
    static int parallelism = 20;
    static ExecutorService executorService = Executors.newWorkStealingPool(parallelism);
    //static ConnectionHolder connectionHolder;
    static List<Long> allUserIds = readFromExcel(config.getExcelPath());


    static Map<Long, List<UserQuestion>> getUserIdToQuestionsMap(final ConnectionHolder connectionHolder,
                                                                 final Set<Long> allQuestionIds, Map<Long, List<Long>> partitionToUserIdMap,
                                                                 Predicate<UserQuestion> meetConditionsUserPredicate) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(partitionToUserIdMap.size());
        Map<Long, List<UserQuestion>> userIdToQuestionsMap = new ConcurrentHashMap<>(64);
        String questionIdStr = StringUtils.join(allQuestionIds, ",");

        int minDoQuestionSize = config.getDoQuestionSize(); //(int) (allQuestionIds.size() * doQuestionRate)
        for (Map.Entry<Long, List<Long>> entry : partitionToUserIdMap.entrySet()) {
            CompletableFuture.supplyAsync(() -> {
                List<UserQuestion> userQuestionList = new ArrayList<>();
                String sql = String.format(userQuestionSql,
                        entry.getKey(), StringUtils.join(entry.getValue(), ","), questionIdStr);
                //System.out.println(sql);
                Connection connection = connectionHolder.getConnection();
                try (Statement statement = connection.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(sql);
                    //long costTime = System.currentTimeMillis() - startTime;
                    while (resultSet.next()) {
                        UserQuestion uq = new UserQuestion(resultSet);
                        if (meetConditionsUserPredicate.test(uq)) {
                            userQuestionList.add(uq);
                        }
                    }
                    //System.out.println("query cost " + costTime + " ,result.size = " + userQuestionList.size());
                    resultSet.close();
                    //System.out.println("完成查询做题记录子任务 " + entry.getKey() + ", 题目数量 " + userQuestionList.size()
                    //        + " ,耗时（ms） " + (System.currentTimeMillis() - startTime));
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println("error sql = " + sql);
                }
                return userQuestionList;
                // 转换 userQuestionList 为 userIdToQuestionsMap 的一部分
            }, BaseUserStat.executorService).thenAcceptAsync(((userQuestions) -> {
                Map<Long, List<UserQuestion>> subQuestionMap = userQuestions.stream().collect(Collectors.groupingBy(UserQuestion::getUserId));
                subQuestionMap.forEach((key,list) -> {
                    if (list.size() >= minDoQuestionSize) {
                        userIdToQuestionsMap.put(key, list);
                    }
                });
                countDownLatch.countDown();
            }), BaseUserStat.executorService);
            //break;
        }
        countDownLatch.await();
        System.out.println("查询一个标签或一批标签对应题目的学员做题数据，耗时 " + (System.currentTimeMillis() - startTime));

        // Blocks until all tasks have completed execution after a shutdown request, instead of CountDownLatch
        //executorService.awaitTermination(6, TimeUnit.MINUTES);
        //System.out.println("所有用户的总做题记录数 = " + userIdToQuestionsMap.values().stream().mapToInt(Collection::size).sum());  // 16_5733
        //System.out.println("所有用户的总做题记录数 = " + userQuestions.size());  // 16_5733
        return userIdToQuestionsMap;
    }

    static void parseTreeStructure(final TagOrKnowledge.TreeNode tagTreeRoot, final List<TagOrKnowledge> allTags, List<Long> parentTagIds) {
        List<Long> finalParentTagIds = parentTagIds;
        allTags.removeIf(tag -> finalParentTagIds.contains(tag.getId()));
        while (!allTags.isEmpty()) {
            List<Long> finalParentTagIds1 = parentTagIds;
            parentTagIds = allTags.stream()
                    .filter(tag -> finalParentTagIds1.contains(tag.getParentId()))
                    .map(TagOrKnowledge::getId).collect(Collectors.toList());
            tagTreeRoot.grow(allTags);
            List<Long> finalParentTagIds2 = parentTagIds;
            allTags.removeIf(tag -> finalParentTagIds2.contains(tag.getId()));
        }
    }

    static List<TagOrKnowledge> getAllKnowledgeByTreeId(final long treeId, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(
                    String.format("select id,parent_id,name from `question`.`knowledge` where root_id = %s and status = 1", treeId));
            return (List<TagOrKnowledge>) ResultSetUtils.parseCollection(resultSet, new ArrayList<>(), TagOrKnowledge.class, true);
        }
    }

    static List<TagOrKnowledge> getAllTagByTreeId(final long treeId, final Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
             ResultSet tagResultSet = statement.executeQuery(
                     String.format("select id,parent_id,name from `question`.`tag` where treeId = %s and status = 1 and type = 1", treeId));
            return (List<TagOrKnowledge>) ResultSetUtils.parseCollection(tagResultSet, new ArrayList<>(), TagOrKnowledge.class, true);
        }
    }

    static List<TagOrKnowledge> getAllTagByParentId(final long treeId, final Connection connection) throws SQLException {
        String sql = String.format("select id,parent_id,name from `question`.`tag` where id = %s and status = 1 and type = 1", treeId);
        ResultSet tagResultSet = connection.createStatement().executeQuery(sql);
        List<TagOrKnowledge> subList = (List<TagOrKnowledge>) ResultSetUtils.parseCollection(tagResultSet, new ArrayList<>(), TagOrKnowledge.class);

        List<TagOrKnowledge> allTags = subList.isEmpty() ? subList : new ArrayList<>(subList);
        while (!subList.isEmpty()) {
            List<Long> nextParentIds = subList.stream().map(TagOrKnowledge::getId).collect(Collectors.toList());
            ResultSet resultSet = connection.createStatement().executeQuery(String.format(
                    "select id,parent_id,name from `question`.`tag` where parent_id in (%s) and status = 1 and type = 1",
                    StringUtils.join(nextParentIds, ",")));
            subList.clear();
            subList = (List<TagOrKnowledge>) ResultSetUtils.parseCollection(resultSet, subList, TagOrKnowledge.class);
            if (!subList.isEmpty()) {
                allTags.addAll(subList);
            }
        }
        return allTags;
    }

    static List<TagOrKnowledge> getNeedQueryTreeNodes(final TagOrKnowledge.TreeNode treeRoot, final Connection connection) throws SQLException {
        final boolean onlyLeafNode = config.onlyLeafNode();
        List<TagOrKnowledge> allTags;
        boolean isTreeId = config.isTreeId();
        boolean isForTag = config.isForTag();
        if (isForTag && isTreeId) {
            allTags = getAllTagByTreeId(config.getTreeRoot().getLeft(), connection);
        }
        else if (!isForTag && isTreeId) {
            allTags = getAllKnowledgeByTreeId(config.getTreeRoot().getLeft(), connection);
        }
        else if (isForTag && !isTreeId){
            allTags = getAllTagByParentId(config.getTreeRoot().getLeft(), connection);
        } else {
            System.err.println("getTagTreeNodes error : not support");
            allTags = Collections.emptyList();
        }

        if (!onlyLeafNode) {
            return allTags;
        }

        if (isTreeId) {
            List<Long> parentTagIds = allTags.stream()
                    .filter(tag -> 0 == tag.getParentId())
                    .map(tag -> {
                        treeRoot.getChildNodes().add(new TagOrKnowledge.TreeNode(tag.getName(), tag.getId(), tag.getParentId()));
                        return tag.getId();
                    }).collect(Collectors.toList());
            parseTreeStructure(treeRoot, allTags, parentTagIds);
        } else {
            parseTreeStructure(treeRoot, allTags, Collections.singletonList(treeRoot.getId()));
        }
        return TagOrKnowledge.TreeNode.getAllLeafTag(treeRoot);
    }

    static List<Statistics> countByCorrectRate(
            final List<UserQuestionSummary> userSummaryList) {
        MultiValuedMap<Integer, UserQuestionSummary> multiValuedMap = new ArrayListValuedHashMap<>();
        for (UserQuestionSummary userQuestionSummary : userSummaryList) {
            int percentCorrectRate = (int) (userQuestionSummary.getAverageCorrectRate() * 100);
            if (percentCorrectRate == 100) {
                multiValuedMap.put(percentCorrectRate / config.levelSeparate - 1, userQuestionSummary);
            } else {
                multiValuedMap.put(percentCorrectRate / config.levelSeparate, userQuestionSummary);
            }
        }
        return multiValuedMap.asMap().entrySet().stream().map(entry -> {
            Pair<Double, Integer> correctRateAndSpeed = getSummaryCorrectRateAndSpeed((List<UserQuestionSummary>)entry.getValue());
            return new Statistics("", entry.getKey()+1, correctRateAndSpeed.getLeft(), correctRateAndSpeed.getRight(), entry.getValue().size());
        }).collect(Collectors.toList());
    }

    /**
     * 根据标签id查question
     */
    static Set<Long> getAllQuestionIdByTagId(final Connection connection, List<Long> tagIds) throws SQLException {
        if (CollectionUtils.isEmpty(tagIds)) {
            return Collections.emptySet();
        }
        ResultSet resultSet;
        Set<Long> questionIds = new HashSet<>();
        if (tagIds.size() == 1) {
            String queryQuestionSql = "select question_id from `question`.`question_tag` where tag_id = %s and status = 1";
            resultSet = connection.createStatement().executeQuery(String.format(queryQuestionSql, tagIds.get(0)));
        } else {
            String queryQuestionSql = "select question_id from `question`.`question_tag` where tag_id in (%s) and status = 1";
            resultSet = connection.createStatement().executeQuery(String.format(queryQuestionSql, StringUtils.join(tagIds, ",")));
        }
        while (resultSet.next()) {
            questionIds.add(resultSet.getLong(1));
        }

        return filterQuestionIds(questionIds, connection);
    }

    /**
     * 汇总一批学员做题数据的平均正确率（left）和平均答题速度（right）
     */
    static Pair<Double, Integer> getSummaryCorrectRateAndSpeed(List<UserQuestionSummary> summaryList) {
        if (summaryList.isEmpty()) {
            return new ImmutablePair<>(-1d, -1);
        }

        if (config.getLowestUserData) {
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

    static void outputToExcel(List<Statistics> statistics, Function<Statistics, Object> levelGetter, String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
            ExcelUtil.exportCommonData(statistics,
                    Arrays.asList("标签名称", "档位", "正确率", "答题速度(秒)", "人数"),
                    Arrays.asList(Statistics::getName, levelGetter, Statistics::getCorrectRateDesc, Statistics::getSpeedSecond, Statistics::getCount),
                    outputStream);
        } catch (IOException e) {
            System.err.println("Excel export error");
            e.printStackTrace();
        }
    }

    /**
     * 按人数分档，输出统计结果
     */
    static List<Statistics> countByUserQuantity(List<UserQuestionSummary> userSummaryList, String tagName) {
        int currentIndex = 0;
        int totalUserCount = userSummaryList.size();
        int levelSize = totalUserCount * config.levelSeparate / 100;
        int remainder = levelSize % totalUserCount;
        levelSize = (remainder > 6 && totalUserCount > (200 / config.levelSeparate))  ? levelSize + 1 : levelSize;

        ArrayList<Statistics> statisticsList = new ArrayList<>(21);
        while (currentIndex < totalUserCount) {
            int toIndex = Math.min(currentIndex + levelSize, totalUserCount);
            List<UserQuestionSummary> subList = userSummaryList.subList(currentIndex, toIndex);

            Pair<Double, Integer> correctRateAndSpeed = getSummaryCorrectRateAndSpeed(subList);
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

    /**
     * 根据知识点id查题
     */
    static Set<Long> getAllQuestionIdByKnowledgeId(final Connection connection, List<Long> allTagIds) throws SQLException {
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
        return filterQuestionIds(questionIds, connection);
    }

    static Set<Long> filterQuestionIds(Set<Long> questionIds, Connection connection) throws SQLException {
        Boolean getSubjective = config.getSubjective;
        if (CollectionUtils.isEmpty(questionIds) || getSubjective == null) {
            return questionIds;
        }
        // 查询主观题id
        ResultSet resultSet = connection.createStatement().executeQuery(
                String.format("select id from `question`.`question` where id in (%s) and type = 3",
                        StringUtils.join(questionIds, ",")));
        List<Long> subjectiveQuestionIds = new ArrayList<>();
        while (resultSet.next()) {
            subjectiveQuestionIds.add(resultSet.getLong(1));
        }

        if (getSubjective) {
            return new HashSet<>(subjectiveQuestionIds);
        } else if (CollectionUtils.isNotEmpty(subjectiveQuestionIds)) {
            questionIds.removeIf(subjectiveQuestionIds::contains);
        }
        return questionIds;
    }

    /**
     * 根据用户的答题记录（user_question_log）*加权*计算用户的答题速度和正确率
     */
    static UserQuestionSummary buildUserQuestionSummaryByQuestionLogs(Long userId, Collection<UserQuestionLog> questionLogs) {
        if (questionLogs == null || questionLogs.isEmpty()) {
            return null;
        }
        // 正确率：权重* 总正确题数/总题数     答题速度：权重* 总时间/总题数
        double totalCorrectRate = 0d;
        int totalAverageSpeed = 0, totalWeight = 0;
        Map<Integer, List<UserQuestionLog>> sceneToQuestionLogsMap = questionLogs.stream().collect(Collectors.groupingBy(UserQuestionLog::getScenesKey));
        for (Map.Entry<Integer, List<UserQuestionLog>> entry : sceneToQuestionLogsMap.entrySet()) {
            int weight = UserQuestionLog.SCENE_WEIGHT.getOrDefault(entry.getKey(), 1);
            int sceneCostTimeSum = entry.getValue().stream().mapToInt(UserQuestionLog::getCostTime).sum();
            int sceneCorrectCount = (int)entry.getValue().stream().filter(UserQuestionLog::getCorrect).count();
            totalWeight += weight;
            totalAverageSpeed += weight * (sceneCostTimeSum / entry.getValue().size());
            totalCorrectRate += weight * ((double) sceneCorrectCount / entry.getValue().size());
        }

        return new UserQuestionSummary(userId, totalAverageSpeed / totalWeight, totalCorrectRate / totalWeight);
    }

    static List<Long> readFromExcel(final String excelPath) {
        List<Long> result = new ArrayList<>(2000);
        try (InputStream fis = new FileInputStream(excelPath)) {
            Workbook workbook = null;
            if (excelPath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (excelPath.endsWith(".xls") || excelPath.endsWith(".et")) {
                workbook = new HSSFWorkbook(fis);
            }

            /* 读EXCEL文字内容 */
            // 获取第一个sheet表，也可使用sheet表名获取
            Sheet sheet = workbook.getSheetAt(0);
            // 获取行
            Iterator<Row> rows = sheet.rowIterator();
            Row row;

            rows.next();  // 跳过第一行的表头
            while (rows.hasNext()) {
                row = rows.next();
                // 获取单元格
                result.add((long) row.getCell(0).getNumericCellValue());
            }
            System.out.println("读取excel数据，totalSize = " + result.size());
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

}
