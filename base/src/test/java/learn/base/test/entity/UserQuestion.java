package learn.base.test.entity;

import learn.base.test.UserStat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collection;

/**
 * 正确率=correct_count/answer_count ; 做题速度=sum_cost_time/answer_count
 * @author Zephyr
 * @date 2021/4/8.
 */
@Getter
@Setter
public class UserQuestion {
    public static String userQuestionSql = "select user_id,question_id,answer_count,sum_cost_time,correct_count " +
            "from user_question_%s \n where user_id in (%s) \n and question_id in (%s) and answer_count > 0 and sum_cost_time > 0";

    private Long userId;
    private Long questionId;
    private Integer answerCount;
    private Integer sumCostTime;
    private Integer correctCount;
    // 做题速度
    //private Integer speed;
    // 正确率
    //private double correctRate;


    public UserQuestion(ResultSet resultSet) throws SQLException {
        this.userId = resultSet.getLong(1);
        this.questionId = resultSet.getLong(2);
        this.answerCount = resultSet.getInt(3);
        this.sumCostTime = resultSet.getInt(4);
        this.correctCount = resultSet.getInt(5);
        //calQuestionStat();
    }

    /**
     * 每个用户的答题统计
     */
    @Getter
    public static class UserQuestionSummary implements Comparable<UserQuestionSummary> {
        public Long userId;
        // 平均做题速度
        private Integer averageSpeed;
        // 平均正确率
        private Double averageCorrectRate;
        // 总做题数
        private Integer totalCount;

        public UserQuestionSummary(Collection<UserQuestion> collection) {
            if (collection == null || collection.isEmpty()) {
                throw new IllegalArgumentException("UserQuestion can't be empty.");
            }
            int totalCorrectCount = collection.stream().mapToInt(UserQuestion::getCorrectCount).sum();
            int totalCostTime = collection.stream().mapToInt(UserQuestion::getSumCostTime).sum();
            int totalAnswerCount = collection.stream().mapToInt(UserQuestion::getAnswerCount).sum();

            this.userId = collection.iterator().next().getUserId();
            this.averageCorrectRate = (double) totalCorrectCount / totalAnswerCount;
            this.averageSpeed = totalCostTime / totalAnswerCount;
            this.totalCount = collection.size();
        }

        @Override
        public String toString() {
            return "UserSummary: correctRate = " + averageCorrectRate + " , speed = " + averageSpeed;
        }

        /**
         * 按用户正确率排序
         */
        @Override
        public int compareTo(final UserQuestionSummary other) {
            if (UserDataConfig.COMPARE_SPEED) {
                return other.averageSpeed.compareTo(this.averageSpeed);
            } else {
                return this.averageCorrectRate.compareTo(other.averageCorrectRate);
            }
        }
    }


    @Getter
    @AllArgsConstructor
    public static class Statistics implements Serializable, Comparable<Statistics> {
        public static String PRINT_TEMPLATE = "档位%s , 平均正确率为 %.2f%%  ， 平均每道题的答题速度为 %d 秒 ，对应总用户数 %d 人";

        static int separate = UserStat.separate;
        private String name;
        private Integer level;
        private Double correctRate;
        private Integer speed;  // 单位：毫秒
        private Integer count;

        //public Statistics(String name, Integer level, Double correctRate, Integer speed, Integer count) {
        //    this.name = name;
        //    this.level = level;
        //    this.correctRate = correctRate * 100;
        //    this.speed = speed;
        //    this.count = count;
        //}

        public static Statistics EMPTY = new Statistics("", 0, 0d, 0, 0);

        public String getPercentDesc() {
            int i = level * separate;
            if (i == 100) {
                return "100% - 100%";
            } else {
                return String.format("%d%% - %d%%", i, (level + 1) * separate);
            }
        }

        public String getCorrectRateDesc() {
            if (correctRate < 0) {
                return "-";
            }
            if (correctRate == 0) {
                return "0%";
            }
            return new DecimalFormat("#0.00%").format(correctRate);
        }

        public Integer getSpeedSecond() {
            return speed / 1000;
        }

        public String toFormatString() {
            return String.format(PRINT_TEMPLATE, level, correctRate, getSpeedSecond(), count);
        }

        @Override
        public int compareTo(final Statistics o) {
            return this.correctRate.compareTo(o.correctRate);
        }

        public void setLevel(final Integer level) {
            this.level = level;
        }
    }

}
