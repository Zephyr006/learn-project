package learn.base.test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import learn.base.utils.ExcelUtil;
import learn.base.utils.StopWatch;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2021/4/7.
 */
public class UserStat {

    static String excelPath = "/Users/wangshidong/Downloads/工作簿12.xlsx";
    static String excelSavePath = "/Users/wangshidong/Desktop/学员做题数据统计.xlsx";
    public static int separate = 5;  // 5%为一档

    public static void main(String[] args) {
        StopWatch stopWatch = StopWatch.createAndStart("学员做题情况统计");
        List<Long> allUserIds = readFromExcel(excelPath);
        System.out.println("\n" + stopWatch.prettyPrint());

        // 标签树id
        long tagTreeId = 30L;
        // 答题覆盖率，低于此做题比例的学员不参与统计
        double doQuestionRate = 0.5;
        int questionSizeThreshold = 5;
        //TagTreeNode tagTreeRoot = new TagTreeNode(tagTreeId, null);

        String host = "";
        String dbName = "relation";
        String username = "";
        String password = "";
        HikariConfig hikariConfig = new HikariConfig(HikariConnectProps.initProps(host, dbName, username, password));
        try (final HikariDataSource dataSource = new HikariDataSource(hikariConfig);
             final Connection connection = dataSource.getConnection()) {
            System.out.println("\nDB connected ? " + connection.isValid(4));


            // 1 以tagTreeId做查询条件查询标签
            List<QuestionTag> allTags = UserStat.getAllQuestionTags(tagTreeId, connection);
            List<Long> allTagIds = allTags.stream().map(QuestionTag::getId).collect(Collectors.toList());
            //List<Long> parentTagIds = allTags.stream().filter(tag -> 0 == tag.getParentId()).map(QuestionTag::getId).collect(Collectors.toList());
            //tagTreeRoot.childNodes.addAll(parentTagIds.stream().map(tagId -> new TagTreeNode(tagId, tagTreeId)).collect(Collectors.toList()));
            // 1.1 解析标签树
            //UserStat.parseTagTree(tagTreeRoot, allTags, parentTagIds);


            // 2. 查询标签下的题目question
            Set<Long> allQuestionIds = UserStat.getAllQuestionIds(connection, allTagIds);
            System.out.println("查询完所有标签及题目元信息  " + stopWatch.prettyPrint());


            // 3. 查询学员做题记录： 正确率=correct_count/answer_count ; 做题速度=sum_cost_time/answer_count
            Map<Long, List<Long>> userIdMap = allUserIds.stream().collect(Collectors.groupingBy(userId -> userId % 100));
            CountDownLatch countDownLatch = new CountDownLatch(userIdMap.size());
            ConcurrentMap<Long, List<UserQuestion>> userIdToQuestionsMap = UserStat.getUserIdToQuestionsMap(dataSource, allQuestionIds, userIdMap, countDownLatch);
            System.err.println("查询完所有用户做题记录  " + stopWatch.prettyPrint());


            // 4. 标签覆盖率：删除不满足标签覆盖率的用户
            double allQuestionCount = allQuestionIds.size();
            Iterator<Map.Entry<Long, List<UserQuestion>>> iterator = userIdToQuestionsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                //List<UserQuestion> userQuestions1 = iterator.next().getValue();
                if (iterator.next().getValue().size() / allQuestionCount  < doQuestionRate) {
                    iterator.remove();
                    break;
                }
            }



            // 5. 按用户正确率排列，计算每档的平均正确率和平均做题速度；
            List<UserQuestion.UserQuestionSummary> userSummaryList = userIdToQuestionsMap.values().stream()
                    .map(UserQuestion.UserQuestionSummary::new)
                    .sorted().collect(Collectors.toList());
            System.out.println(String.format("样本总数 %d 人，其中满足题目覆盖率 %.0f%% 的用户 %d 人",
                    allUserIds.size(), doQuestionRate*100, userSummaryList.size()));
            // 按人数分档
            //outputByUserCount(userSummaryList);
            // 按正确率分档
            Map<Integer, Collection<UserQuestion.UserQuestionSummary>> multiValuedMap = UserStat.getStatSummaryMultiValuedMap(userSummaryList);
            outputByCorrectRate(multiValuedMap);


        } catch (SQLException | InterruptedException | FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(stopWatch.stopAndPrint());
    }

    /**
     * 按人数分档，输出统计结果
     */
    private static void outputByUserCount(List<UserQuestion.UserQuestionSummary> userSummaryList) {
        int currentIndex = 0;
        int totalUserCount = userSummaryList.size();
        int levelSize = (int) (totalUserCount * ((double) separate / 100));
        boolean hasRemainder = levelSize % totalUserCount > 0;
        levelSize = hasRemainder ? levelSize + 1 : levelSize;

        while (currentIndex < totalUserCount) {
            int toIndex = Math.min(currentIndex + levelSize, totalUserCount);
            List<UserQuestion.UserQuestionSummary> subList = userSummaryList.subList(currentIndex, toIndex);

            double averageCorrectRate = subList.stream()
                    .mapToDouble(UserQuestion.UserQuestionSummary::getAverageCorrectRate).average().getAsDouble();
            double averageSpeed = subList.stream()
                    .mapToInt(UserQuestion.UserQuestionSummary::getAverageSpeed).average().getAsDouble();
            currentIndex = toIndex;
            if ((currentIndex + 1) % levelSize > 0) {
                consoleOutput((currentIndex + 1) / levelSize + 1, averageCorrectRate, (int) averageSpeed, subList.size());
            } else {
                consoleOutput((currentIndex + 1) / levelSize, averageCorrectRate, (int) averageSpeed, subList.size());
            }
        }
    }

    private static void consoleOutput(int level, double correctRate, int speedMs, int size) {
        //String outputTemplate = "档位%s , 平均正确率为 %.2f%%  ， 平均每道题的答题速度为 %d 秒 ，对应总用户数 %d 人.";
        System.out.println(String.format("档位%s , 平均正确率为 %.2f%%  ， 平均每道题的答题速度为 %d 秒 ，对应总用户数 %d 人.",
                level, correctRate * 100, speedMs / 1000, size));
    }

    private static void parseTagTree(final TagTreeNode tagTreeRoot, final List<QuestionTag> allTags, List<Long> parentTagIds) {
        List<Long> finalParentTagIds = parentTagIds;
        allTags.removeIf(tag -> finalParentTagIds.contains(tag.getId()));
        while (!allTags.isEmpty()) {
            List<Long> finalParentTagIds1 = parentTagIds;
            parentTagIds = allTags.stream()
                    .filter(tag -> finalParentTagIds1.contains(tag.getParentId()))
                    .map(QuestionTag::getId).collect(Collectors.toList());
            tagTreeRoot.grow(allTags);
            List<Long> finalParentTagIds2 = parentTagIds;
            allTags.removeIf(tag -> finalParentTagIds2.contains(tag.getId()));
        }
    }

    private static List<QuestionTag> getAllQuestionTags(final long tagTreeId, final Connection connection) throws SQLException {
        List<QuestionTag> allTags = new ArrayList<>();
        ResultSet tagResultSet = connection.createStatement().executeQuery(
                String.format("select id,parentId,name from `public-admin`.`questiontag` where treeId = %s and status = 0 and type = 1", tagTreeId));
        while (tagResultSet.next()) {
            allTags.add(new QuestionTag(tagResultSet.getLong(1), tagResultSet.getLong(2), tagResultSet.getString(3)));
        }
        return allTags;
    }

    /**
     * 按正确率分档，统计结果
     */
    private static void outputByCorrectRate(
                final Map<Integer, Collection<UserQuestion.UserQuestionSummary>> multiValuedMap) throws FileNotFoundException {

        List<UserQuestion.UserStatistics> statistics = multiValuedMap.entrySet().stream().map(entry -> {
            double averageCorrectRate = entry.getValue().stream()
                    .mapToDouble(UserQuestion.UserQuestionSummary::getAverageCorrectRate).average().getAsDouble();
            double averageSpeed = entry.getValue().stream()
                    .mapToInt(UserQuestion.UserQuestionSummary::getAverageSpeed).average().getAsDouble();
            return new UserQuestion.UserStatistics(entry.getKey(), averageCorrectRate * 100, (int) averageSpeed / 1000, entry.getValue().size());
        }).collect(Collectors.toList());
        ExcelUtil.doExport(statistics, Arrays.asList("档位", "正确率", "答题速度(秒)", "人数"),
                Arrays.asList(UserQuestion.UserStatistics::getLevelDesc, UserQuestion.UserStatistics::getCorrectRateDesc, UserQuestion.UserStatistics::getSpeed, UserQuestion.UserStatistics::getCount),
                new FileOutputStream(new File(excelSavePath)));
        //for (Map.Entry<Integer, Collection<UserQuestion.UserQuestionSummary>> entry : multiValuedMap.asMap().entrySet()) {
        //    double averageCorrectRate = entry.getValue().stream()
        //            .mapToDouble(UserQuestion.UserQuestionSummary::getAverageCorrectRate).average().getAsDouble();
        //    double averageSpeed = entry.getValue().stream()
        //            .mapToInt(UserQuestion.UserQuestionSummary::getAverageSpeed).average().getAsDouble();
        //    System.out.println(String.format(outputTemplate, entry.getKey(), averageCorrectRate * 100, (int)averageSpeed/1000, entry.getValue().size()));
        //}
    }

    private static Map<Integer, Collection<UserQuestion.UserQuestionSummary>> getStatSummaryMultiValuedMap(
            final List<UserQuestion.UserQuestionSummary> userSummaryList) {
        MultiValuedMap<Integer, UserQuestion.UserQuestionSummary> multiValuedMap = new ArrayListValuedHashMap<>();
        for (UserQuestion.UserQuestionSummary userQuestionSummary : userSummaryList) {
            int percentCorrectRate = (int) (userQuestionSummary.getAverageCorrectRate() * 100);
            multiValuedMap.put(percentCorrectRate / separate, userQuestionSummary);
        }
        return multiValuedMap.asMap();
    }

    private static ConcurrentMap<Long, List<UserQuestion>> getUserIdToQuestionsMap(final HikariDataSource dataSource,
                   final Set<Long> allQuestionIds, final Map<Long, List<Long>> userIdMap, final CountDownLatch countDownLatch) throws InterruptedException {
        ConcurrentMap<Long, List<UserQuestion>> userIdToQuestionsMap = new ConcurrentHashMap<>(2048);
        String questionIdStr = StringUtils.join(allQuestionIds, ",");

        int parallelism = 25;
        System.out.println("并发查询用户相关做题记录，并发线程数 = " + parallelism);
        ExecutorService executorService = Executors.newFixedThreadPool(parallelism);
        for (Map.Entry<Long, List<Long>> entry : userIdMap.entrySet()) {
            CompletableFuture.runAsync(() -> {
                try (Connection connection2 = dataSource.getConnection();
                     Statement statement = connection2.createStatement()) {
                    ResultSet resultSet = statement.executeQuery(String.format(
                            UserQuestion.userQuestionSql, entry.getKey(), StringUtils.join(entry.getValue(), ","), questionIdStr));
                    while (resultSet.next()) {
                        UserQuestion userQuestion = new UserQuestion(resultSet);
                        userIdToQuestionsMap.compute(resultSet.getLong(1), (k,v) -> {
                            if (v == null) {
                                List<UserQuestion> list = new ArrayList<>(64);
                                list.add(userQuestion);
                                return list;
                            } else {
                                v.add(userQuestion);
                                return v;
                            }
                        });
                    }
                    resultSet.close();
                    System.out.println("完成查询做题记录任务 " + entry.getKey());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }, executorService);
        }
        countDownLatch.await();
        executorService.shutdown();
        System.out.println("所有用户的总做题记录数 = " + userIdToQuestionsMap.values().stream().mapToInt(Collection::size).sum());  // 16_5733
        //System.out.println("所有用户的总做题记录数 = " + userQuestions.size());  // 16_5733
        return userIdToQuestionsMap;
    }

    private static Set<Long> getAllQuestionIds(final Connection connection, final List<Long> allTagIds) throws SQLException {
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


    static class TagTreeNode {
        Long id;
        Long parentId;
        List<TagTreeNode> childNodes = new ArrayList<>();

        public TagTreeNode(final Long id, final Long parentId) {
            this.id = id;
            this.parentId = parentId;
        }

        public boolean grow(List<QuestionTag> tags) {
            if (CollectionUtils.isEmpty(tags)) {
                return false;
            }

            boolean grew = false;
            if (CollectionUtils.isEmpty(childNodes)) { //叶子节点
                for (QuestionTag questionTag : tags) {
                    if (questionTag.getParentId().compareTo(id) == 0) {
                        childNodes.add(new TagTreeNode(questionTag.getId(), questionTag.getParentId()));
                        grew = true;
                    }
                }
            } else {
                for (TagTreeNode tagNode : childNodes) {
                    grew = Boolean.logicalOr(grew, tagNode.grow(tags));
                }
            }
            return grew;
        }

        public static List<Long> getAllLeafTagIds(TagTreeNode root) {
            List<Long> leafTagIds = new ArrayList<>();
            for (TagTreeNode childNode : root.childNodes) {
                if (childNode.childNodes.isEmpty()) {
                    leafTagIds.add(childNode.id);
                } else {
                    for (TagTreeNode node : childNode.childNodes) {
                        leafTagIds.addAll(TagTreeNode.getAllLeafTagIds(node));
                    }
                }
            }
            return leafTagIds;
        }
    }


    private static <V> List<Long> readFromExcel(final String excelPath) {
        List<Long> result = new ArrayList<>(2 << 14);
        try (InputStream fis = new FileInputStream(excelPath)) {
            Workbook workbook = null;
            if (excelPath.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (excelPath.endsWith(".xls") || excelPath.endsWith(".et")) {
                workbook = new HSSFWorkbook(fis);
            }
            fis.close();

            /* 读EXCEL文字内容 */
            // 获取第一个sheet表，也可使用sheet表名获取
            Sheet sheet = workbook.getSheetAt(0);
            // 获取行
            Iterator<Row> rows = sheet.rowIterator();
            Row row;
            XSSFCell cell;

            rows.next();
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


    static class QuestionTag {
        private Long id;
        private Long parentId;
        private String name;

        public QuestionTag(final Long id, final Long parentId, String name) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public Long getParentId() {
            return parentId;
        }

        public void setParentId(final Long parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }


}
