package learn.base.test;

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
            "from user_question_%s \n where user_id in (%s) \n and question_id in (%s) and answer_count > 0";

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


    public static class UserQuestionSummary implements Comparable<UserQuestionSummary> {
        public Long userId;
        // 平均做题速度
        private Integer averageSpeed;
        // 平均正确率
        private Double averageCorrectRate;

        public UserQuestionSummary(Collection<UserQuestion> collection) {
            if (collection == null || collection.isEmpty()) {
                throw new IllegalArgumentException("UserQuestion can't be empty.");
            }
            int totalCorrectCount = collection.stream().mapToInt(UserQuestion::getCorrectCount).sum();
            int totalCostTime = collection.stream().mapToInt(UserQuestion::getSumCostTime).sum();
            int totalAnswerCount = collection.stream().mapToInt(UserQuestion::getAnswerCount).sum();

            this.userId = new Long(collection.iterator().next().getUserId());
            this.averageCorrectRate = (double) totalCorrectCount / totalAnswerCount;
            this.averageSpeed = totalCostTime / totalAnswerCount;
        }

        @Override
        public String toString() {
            return "UserSummary: userId = " + userId + ", correctRate = " + averageCorrectRate;
        }

        @Override
        public int compareTo(final UserQuestionSummary o) {
            return this.averageCorrectRate.compareTo(o.averageCorrectRate);
        }

        public Integer getAverageSpeed() {
            return averageSpeed;
        }

        public Double getAverageCorrectRate() {
            return averageCorrectRate;
        }
    }

    public static class UserStatistics implements Serializable {
        public static String PRINT_TEMPLATE = "档位%s , 平均正确率为 %.2f%%  ， 平均每道题的答题速度为 %d 秒 ，对应总用户数 %d 人";

        static int separate = UserStat.separate;
        private Integer level;
        private Double correctRate;
        private Integer speed;
        private Integer count;

        public UserStatistics(final Integer level, final Double correctRate, final Integer speed, final Integer count) {
            this.level = level;
            this.correctRate = correctRate;
            if (level > 1 && correctRate < 1) {
                this.correctRate *= 100;
            }
            this.speed = speed;
            this.count = count;
        }

        public String getLevelRateDesc() {
            int i = level * separate;
            if (i == 100) {
                return "100% - 100%";
            } else {
                return String.format("%d%% - %d%%", i, (level + 1) * separate);
            }
        }

        public String getCorrectRateDesc() {
            return new DecimalFormat("#00.00").format(correctRate) + "%";
        }

        public Integer getSpeed() {
            return speed;
        }

        public Integer getCount() {
            return count;
        }

        public String toFormatString() {
            return String.format(PRINT_TEMPLATE, level, correctRate, speed / 1000, count);
        }
    }

}
