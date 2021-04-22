package learn.base.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import learn.base.test.entity.Tag;
import learn.base.test.entity.UserQuestion;
import learn.base.utils.ExcelUtil;
import learn.base.utils.HikariConfigUtil;
import learn.base.utils.StopWatch;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import java.util.HashMap;
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
import java.util.stream.Collectors;

import static learn.base.test.entity.UserQuestion.Statistics;
import static learn.base.test.entity.UserQuestion.UserQuestionSummary;
import static learn.base.test.entity.UserQuestion.userQuestionSql;

/**
 * @author Zephyr
 * @date 2021/4/7.
 */
public class UserStat {

    static String excelPath = "/Users/wangshidong/Downloads/工作簿12.xlsx";
    static String excelSavePath = "/Users/wangshidong/Desktop/学员做题数据统计.xlsx";
    static long tagTreeId = 30L;  // 标签树id
    public static int separate = 5;  // 5%为一档
    static double doQuestionRate = 0.001;  // 答题覆盖率，低于此做题比例的学员不参与统计


    public static void main(String[] args) throws FileNotFoundException{
        StopWatch stopWatch = StopWatch.createAndStart("学员做题情况统计");
        List<Long> allUserIds = readFromExcel(excelPath);
        System.out.println("\n" + stopWatch.prettyPrint());


        //TagTreeNode tagTreeRoot = new TagTreeNode(tagTreeId, null);

        String host = "";
        String dbName = "relation";
        String username = "";
        String password = "";
        HikariConfig hikariConfig = new HikariConfig(HikariConfigUtil.initProps(host, dbName, username, password));
        try (final HikariDataSource dataSource = new HikariDataSource(hikariConfig);
             final Connection connection = dataSource.getConnection()) {
            System.out.println("\nDB connected ? " + connection.isValid(4));


            // 1 以tagTreeId做查询条件查询标签
            List<Tag> allTag = UserStat.getAllQuestionTags(tagTreeId, connection);
            List<Long> allTagIds = allTag.stream().map(Tag::getId).collect(Collectors.toList());
            //List<Long> parentTagIds = allTag.stream().filter(tag -> 0 == tag.getParentId()).map(QuestionTag::getId).collect(Collectors.toList());
            //tagTreeRoot.childNodes.addAll(parentTagIds.stream().map(tagId -> new TagTreeNode(tagId, tagTreeId)).collect(Collectors.toList()));
            // 1.1 解析标签树
            //UserStat.parseTagTree(tagTreeRoot, allTag, parentTagIds);


            // 2. 查询标签下的题目question
            Set<Long> allQuestionIds = UserStat.getAllQuestionIdByTagIds(connection, allTagIds);
            System.out.println("查询完所有标签及题目元信息  " + stopWatch.prettyPrint());


            // 3. 查询学员做题记录： 正确率=correct_count/answer_count ; 做题速度=sum_cost_time/answer_count
            //System.out.println(String.format(UserQuestion.userQuestionSql,
            //        45, StringUtils.join(allUserIds.stream().filter(userId -> userId % 100 == 45).collect(Collectors.toList()), ","),
            //        StringUtils.join(allQuestionIds, ",")));
            //System.exit(0);

            Map<Long, List<UserQuestion>> userIdToQuestionsMap = UserStat.getUserIdToQuestionsMap(dataSource, allQuestionIds, allUserIds);
            System.err.println("查询完所有用户做题记录  " + stopWatch.prettyPrint());


            // 4. 标签覆盖率：删除不满足标签覆盖率的用户
            //removeAllIllegalUser(allQuestionIds, userIdToQuestionsMap);
            System.out.println(String.format("样本总数 %d 人，其中满足题目覆盖率 %.000f%% 的用户 %d 人",
                    allUserIds.size(), doQuestionRate*100, userIdToQuestionsMap.size()));


            // 5. 按用户正确率排列，计算每档的平均正确率和平均做题速度；
            List<UserQuestionSummary> userSummaryList = userIdToQuestionsMap.values().stream()
                    .map(UserQuestionSummary::new)
                    .sorted()
                    .collect(Collectors.toList());

            // 按人数分档
            List<Statistics> byUserCount = UserStat.countByUserQuantity(userSummaryList);
            byUserCount.forEach(s -> System.out.println(s.toFormatString()));
            //outputToExcel(byUserCount, Statistics::getLevel, excelSavePath);
            // 按正确率分档
            //List<Statistics> byCorrectRate = UserStat.countByCorrectRate(userSummaryList);
            //byCorrectRate.forEach(s -> System.out.println(s.toFormatString()));
            //outputToExcel(byCorrectRate, Statistics::getPercentDesc, "/Users/wangshidong/Desktop/学员做题数据统计233.xlsx");


        } catch (SQLException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(stopWatch.stopAndPrint());
    }


    private static void outputToExcel(List<Statistics> statistics, Function<Statistics, Object> levelGetter,  String filePath) {
        try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
            ExcelUtil.exportCommonData(statistics,
                    Arrays.asList("档位", "正确率", "答题速度(秒)", "人数"),
                    Arrays.asList(levelGetter, Statistics::getCorrectRateDesc, Statistics::getSpeedSecond, Statistics::getCount),
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
            Set<Long> allQuestionIds, Map<Long, List<UserQuestion>> userIdToQuestionsMap) {
        double allQuestionCount = allQuestionIds.size();
        //List<UserQuestion> userQuestions1 = iterator.next().getValue();
        userIdToQuestionsMap.entrySet().removeIf(longListEntry -> longListEntry.getValue().size() / allQuestionCount < doQuestionRate);
        return userIdToQuestionsMap;
    }

    /**
     * 按人数分档，输出统计结果
     */
    private static List<Statistics> countByUserQuantity(List<UserQuestionSummary> userSummaryList) {
        int currentIndex = 0;
        int totalUserCount = userSummaryList.size();
        int levelSize = (int) (totalUserCount * ((double) separate / 100));
        boolean hasRemainder = levelSize % totalUserCount > 0;
        levelSize = hasRemainder ? levelSize + 1 : levelSize;

        List<Statistics> statisticsList = new ArrayList<>(20);
        while (currentIndex < totalUserCount) {
            int toIndex = Math.min(currentIndex + levelSize, totalUserCount);
            List<UserQuestionSummary> subList = userSummaryList.subList(currentIndex, toIndex);

            double averageCorrectRate = subList.stream()
                    .mapToDouble(UserQuestionSummary::getAverageCorrectRate).average().getAsDouble();
            double averageSpeed = subList.stream()
                    .mapToInt(UserQuestionSummary::getAverageSpeed).average().getAsDouble();
            //currentIndex = toIndex;
            if ((currentIndex = toIndex) % levelSize > 0) {
                int level = (currentIndex) / levelSize + 1;
                statisticsList.add(new Statistics("", level, averageCorrectRate, (int) averageSpeed, subList.size()));
                //consoleOutput(level, averageCorrectRate, (int) averageSpeed, subList.size());
            } else {
                statisticsList.add(new Statistics("", currentIndex / levelSize, averageCorrectRate, (int) averageSpeed, subList.size()));
            }
        }
        return statisticsList;
    }

    private static void parseTagTree(final Tag.TagTreeNode tagTreeRoot, List<Tag> allTags, List<Long> parentTagIds) {
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

    private static List<Tag> getAllQuestionTags(final long tagTreeId, final Connection connection) throws SQLException {
        List<Tag> allTags = new ArrayList<>();
        ResultSet tagResultSet = connection.createStatement().executeQuery(
                String.format("select id,parentId,name from `public-admin`.`questiontag` where treeId = %s and status = 0 and type = 1", tagTreeId));
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
                multiValuedMap.put(percentCorrectRate / separate - 1, userQuestionSummary);
            } else {
                multiValuedMap.put(percentCorrectRate / separate, userQuestionSummary);
            }
        }
        return multiValuedMap.asMap().entrySet().stream().map(entry -> {
            double averageCorrectRate = entry.getValue().stream()
                    .mapToDouble(UserQuestionSummary::getAverageCorrectRate).average().getAsDouble();
            double averageSpeed = entry.getValue().stream()
                    .mapToInt(UserQuestionSummary::getAverageSpeed).average().getAsDouble();
            return new Statistics("", entry.getKey()+1, averageCorrectRate * 100, (int) averageSpeed, entry.getValue().size());
        }).collect(Collectors.toList());
    }

    private static Map<Long, List<UserQuestion>> getUserIdToQuestionsMap(final HikariDataSource dataSource,
                    final Set<Long> allQuestionIds, final List<Long> allUserIds) throws InterruptedException {
        int minDoQuestionSize = (int) (allQuestionIds.size() * doQuestionRate);
        Map<Long, List<Long>> partitionToUserIdMap = allUserIds.stream().collect(Collectors.groupingBy(userId -> userId % 100));
        CountDownLatch countDownLatch = new CountDownLatch(partitionToUserIdMap.size());
        Map<Long, List<UserQuestion>> userIdToQuestionsMap = new ConcurrentHashMap<>(1024);
        String questionIdStr = StringUtils.join(allQuestionIds, ",");

        int parallelism = 20;
        System.out.println("并发查询用户相关做题记录，并发线程数 = " + parallelism);
        ExecutorService executorService = Executors.newFixedThreadPool(parallelism);

        // 使用executorService.invokeAll()一次提交多个callable并返回结果集 -- ??invokeAll方法会阻塞吗？？
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

        for (Map.Entry<Long, List<Long>> entry : partitionToUserIdMap.entrySet()) {
            CompletableFuture.supplyAsync(() -> {
                List<UserQuestion> userQuestionList = new ArrayList<>(888);
                try (Connection connection2 = dataSource.getConnection();
                     Statement statement = connection2.createStatement()) {
                    final long millis = System.currentTimeMillis();
                    final String sql = String.format(userQuestionSql,
                            entry.getKey(), StringUtils.join(entry.getValue(), ","), questionIdStr);
                    ResultSet resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        userQuestionList.add(new UserQuestion(resultSet));
                    }
                    resultSet.close();
                    System.out.println("完成查询做题记录子任务 " + entry.getKey() + ", 题目数量 " + userQuestionList.size() + " ,耗时（ms） " + (System.currentTimeMillis() - millis));
                } catch (SQLException e) {
                    e.printStackTrace();
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

        executorService.shutdown();
        // Blocks until all tasks have completed execution after a shutdown request, instead of CountDownLatch
        //executorService.awaitTermination(6, TimeUnit.MINUTES);
        //System.out.println("所有用户的总做题记录数 = " + userIdToQuestionsMap.values().stream().mapToInt(Collection::size).sum());  // 16_5733
        //System.out.println("所有用户的总做题记录数 = " + userQuestions.size());  // 16_5733
        return userIdToQuestionsMap;
    }

    private static Set<Long> getAllQuestionIdByTagIds(final Connection connection, final List<Long> allTagIds) throws SQLException {
        String queryQuestionSql = "select id from `public-admin`.`question` where FIND_IN_SET('%s',`tagIds`) and status = 0";
        Map<Long, List<Long>> tagToQuestionIdsMap = new HashMap<>();
        //List<Long> allLeafTagIds = TagTreeNode.getAllLeafTagIds(tagTreeRoot);
        for (Long tagId : allTagIds) {
            List<Long> questionIds = new ArrayList<>();
            ResultSet questionR = connection.createStatement().executeQuery(String.format(queryQuestionSql, tagId));
            while (questionR.next()) {
                questionIds.add(questionR.getLong(1));
            }
            //if (questionIds.size() > questionSizeThreshold) {
                tagToQuestionIdsMap.put(tagId, questionIds);
            //}
        }
        Set<Long> allQuestionIds = tagToQuestionIdsMap.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
        System.out.println("allQuestionIds.size = " + allQuestionIds.size());
        //System.out.println("allUserIds.size = " + allUserIds.size());
        return allQuestionIds;
    }



    public static List<Long> readFromExcel(final String excelPath) {
        List<Long> result = new ArrayList<>(2 << 14);
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
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }


}
