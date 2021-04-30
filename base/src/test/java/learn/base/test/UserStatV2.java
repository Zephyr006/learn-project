package learn.base.test;

import com.zaxxer.hikari.HikariDataSource;
import learn.base.test.entity.Statistics;
import learn.base.test.entity.TagOrKnowledge;
import learn.base.test.entity.UserDataConfig;
import learn.base.test.entity.UserQuestion;
import learn.base.utils.DataSourceHolder;
import learn.base.utils.HikariConfigUtil;
import learn.base.utils.StopWatch;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static learn.base.test.entity.UserQuestion.UserQuestionSummary;

/**
 * map_questiontype - 客观：1、2、4 、5    ；主观：3
 * @author Zephyr
 * @date 2021/4/7.
 */
public class UserStatV2 extends UserStatBase {



    public static void main(String[] args) throws InterruptedException {
        StopWatch stopWatch = StopWatch.createAndStart("学员做题情况统计");
        List<Long> allUserIds = readFromExcel(UserStatV2.config.getExcelPath());
        Map<Long, List<Long>> partitionToUserIdMap = allUserIds.stream().collect(Collectors.groupingBy(userId -> userId % 100));
        System.out.println("开始并发查询用户相关做题记录，并发线程数 = " + parallelism + "，学员总数 = " + allUserIds.size());


        try (DataSourceHolder dataSourceHolder = DataSourceHolder.hold(
                new HikariDataSource(HikariConfigUtil.buildHikariConfig(
                        config.dbHostAndUsername().getLeft(), UserDataConfig.dbName,
                        config.dbHostAndUsername().getRight(), UserDataConfig.password)))) {
            Connection mainConnection = dataSourceHolder.getConnection();
            System.out.println("\nDB connected ? " + mainConnection.isValid(2));


            boolean onlyLeafNode = config.onlyLeafNode();
            // 1 以tagTreeId做查询条件查询标签
            TagOrKnowledge.TreeNode treeRoot = new TagOrKnowledge.TreeNode(config.getTreeRoot().getRight(), config.getTreeRoot().getLeft(), null);
            List<TagOrKnowledge> allNeedTag = getTagTreeNodes(treeRoot, mainConnection, onlyLeafNode);
            if (allNeedTag.isEmpty()) {
                System.err.println("要查询的节点个数为0！！！");
                return;
            }
            System.out.println("根节点名称【" + config.getTreeRoot().getRight() + "】，要查询的目标节点个数为 " + allNeedTag.size());

            //查询做题数据
            List<Statistics> statistics;
            if (onlyLeafNode) {
                statistics = queryLeafNodeStatistics(allNeedTag, partitionToUserIdMap, dataSourceHolder);
            } else {
                statistics = queryAllTagNodeStatistics(allNeedTag, partitionToUserIdMap, dataSourceHolder);
            }

            if (CollectionUtils.isNotEmpty(statistics)) {
                outputToExcel(statistics, Statistics::getLevel, config.getExcelSavePath());
            } else {
                System.err.println("Error：没有统计数据可供输出！");
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
        System.out.println(stopWatch.stopAndPrint());
    }

    private static List<Statistics> queryAllTagNodeStatistics(final List<TagOrKnowledge> allNeedTag, final Map<Long, List<Long>> partitionToUserIdMap, final DataSourceHolder dataSourceHolder) throws SQLException, InterruptedException {

        // 2. 查询标签下的题目question
        List<Long> allTagIds = allNeedTag.stream().map(TagOrKnowledge::getId).collect(Collectors.toList());
        Set<Long> allQuestionIds;
        if (config.isForTag()) {
            allQuestionIds = getAllQuestionIdByTagId(dataSourceHolder.getConnection(), allTagIds);
        } else {
            allQuestionIds = getAllQuestionIdByKnowledgeId(dataSourceHolder.getConnection(), allTagIds);
        }
        System.out.println("查询完所有标签及题目元数据，总题目数 = " + allQuestionIds.size());


        Predicate<UserQuestion> meetConditionsUserPredicate = Objects::nonNull;
        Map<Long, List<UserQuestion>> userIdToQuestionsMap = getUserIdToQuestionsMap(dataSourceHolder, allQuestionIds, partitionToUserIdMap, meetConditionsUserPredicate);

        List<Statistics> statisticsList = new ArrayList<>();
        List<UserQuestionSummary> userQuestionSummaryList;
        if (userIdToQuestionsMap.size() >= 60) {
            System.out.println(String.format("满足题目覆盖率要求的用户 %d 人", userIdToQuestionsMap.size()));
            // 5. 按用户正确率排列，计算每档的平均正确率和平均做题速度；
            userQuestionSummaryList = userIdToQuestionsMap.values().stream()
                    .map(UserQuestionSummary::new)
                    .sorted(Comparator.reverseOrder())
                    .collect(Collectors.toList());

            // 按人数分档
            List<Statistics> byUserCount = countByUserQuantity(userQuestionSummaryList, config.getTreeRoot().getRight());
            statisticsList.addAll(byUserCount);

        } else {
            System.err.println("节点【" + config.getTreeRoot().getRight() + "】满足题目覆盖率的人数过少，无法生成分档的做题统计数据");
            userQuestionSummaryList = userIdToQuestionsMap.values().stream()
                    .map(UserQuestionSummary::new).collect(Collectors.toList());
        }
        Pair<Double, Integer> correctRateAndSpeed = getSummaryCorrectRateAndSpeed(userQuestionSummaryList);
        statisticsList.add(new Statistics(config.getTreeRoot().getRight() + " 统计", 0,
                correctRateAndSpeed.getLeft(), correctRateAndSpeed.getRight(), userIdToQuestionsMap.size()));
        return statisticsList;
    }

    static List<Statistics> queryLeafNodeStatistics(final List<TagOrKnowledge> allNeedTag, final Map<Long, List<Long>> partitionToUserIdMap, final DataSourceHolder dataSourceHolder) throws SQLException, InterruptedException {

        // 2. 查询标签下的题目question
        List<Callable<Pair<TagOrKnowledge, Set<Long>>>> questionIdCallableList = allNeedTag.stream().map(tag -> (Callable<Pair<TagOrKnowledge, Set<Long>>>) () -> {
            Set<Long> allQuestionIds;
            Connection connection = dataSourceHolder.getConnection();
            if (config.isForTag()) {
                allQuestionIds = getAllQuestionIdByTagId(connection, Collections.singletonList(tag.getId()));
            } else {
                allQuestionIds = getAllQuestionIdByKnowledgeId(connection, Collections.singletonList(tag.getId()));
            }
            return new ImmutablePair<>(tag, allQuestionIds);
        }).collect(Collectors.toList());

        List<Future<Pair<TagOrKnowledge, Set<Long>>>> futureList = UserStatBase.executorService.invokeAll(questionIdCallableList);
        List<Pair<TagOrKnowledge, Set<Long>>> tagToQuestionIdList = futureList.stream().map(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("跳过了节点的做题情况统计，因为其题目查询出现了异常");
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).filter(pair -> pair.getRight().size() > config.getDoQuestionSize()).collect(Collectors.toList());
        System.out.println("查询完所有标签及题目元数据，总题目数 = " + tagToQuestionIdList.stream().map(Pair::getRight).mapToLong(Collection::size).sum());
        //String questionIds = tagToQuestionIdList.stream().map(Pair::getRight).filter(Objects::nonNull).flatMap(Collection::stream).map(Object::toString)
        //        .collect(Collectors.joining(","));
        //System.out.println(questionIds);
        //System.exit(-1);
        //tagToQuestionIdList.forEach(tagToQuestionIds ->
        //        System.out.println("Tag:" + tagToQuestionIds.getLeft().getName() + ", questionIds.size = " + tagToQuestionIds.getRight().size()));


        // 只统计满足条件的学员做题数据
        List<Statistics> resultList = new ArrayList<>();
        Predicate<UserQuestion> meetConditionsUserPredicate =
                uq -> uq.getSumCostTime() / uq.getAnswerCount() > 500 && !uq.getCorrectCount().equals(uq.getAnswerCount());
        for (Pair<TagOrKnowledge, Set<Long>> tagAndQuestionIds : tagToQuestionIdList) {
            if (tagAndQuestionIds.getRight().size() < config.getDoQuestionSize()) {
                System.err.println(String.format("跳过了节点【%s】的做题情况统计，因为其题目题目数量过少 (%d)",
                        tagAndQuestionIds.getKey().getName(), tagAndQuestionIds.getRight().size()));
                continue;
            }

            List<UserQuestionSummary> userQuestionSummaryList;
            TagOrKnowledge tag = tagAndQuestionIds.getLeft();
            Map<Long, List<UserQuestion>> userIdToQuestionsMap = getUserIdToQuestionsMap(dataSourceHolder, tagAndQuestionIds.getRight(), partitionToUserIdMap, meetConditionsUserPredicate);

            if (userIdToQuestionsMap.size() >= 60) {
                System.out.println(String.format("节点【%s】：满足题目覆盖率要求的用户 %d 人",
                        tag.getName(), userIdToQuestionsMap.size()));
                // 5. 按用户正确率排列，计算每档的平均正确率和平均做题速度；
                userQuestionSummaryList = userIdToQuestionsMap.values().stream()
                        .map(UserQuestionSummary::new)
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());

                // 按人数分档
                List<Statistics> byUserCount = countByUserQuantity(userQuestionSummaryList, tag.getName());
                resultList.addAll(byUserCount);

            } else {
                System.err.println("节点【" + tag.getName() + "】满足题目覆盖率的人数过少，无法生成分档的做题统计数据");
                userQuestionSummaryList = userIdToQuestionsMap.values().stream()
                        .map(UserQuestionSummary::new).collect(Collectors.toList());
            }

            Pair<Double, Integer> correctRateAndSpeed = getSummaryCorrectRateAndSpeed(userQuestionSummaryList);
            resultList.add(new Statistics(tag.getName() + " 统计", 0,
                    correctRateAndSpeed.getLeft(), correctRateAndSpeed.getRight(), userIdToQuestionsMap.size()));

        }

        return resultList;
    }

    static List<TagOrKnowledge> getTagTreeNodes(final TagOrKnowledge.TreeNode treeRoot, final Connection connection, final boolean onlyLeafNode) throws SQLException {
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

}
