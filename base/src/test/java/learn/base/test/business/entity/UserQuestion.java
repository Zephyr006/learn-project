package learn.base.test.business.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
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
            "from user_question_%s \n where user_id in (%s) \n and question_id in (%s) \n and answer_count > 0 and status = 1" +
            " and sum_cost_time > 700";

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
        this.userId = resultSet.getLong("user_id");
        this.questionId = resultSet.getLong("question_id");
        this.answerCount = resultSet.getInt("answer_count");
        this.sumCostTime = resultSet.getInt("sum_cost_time");
        this.correctCount = resultSet.getInt("correct_count");
    }

    /**
     * 一个用户对一批题的答题统计
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserQuestionSummary implements Comparable<UserQuestionSummary> {
        public Long userId;
        // 平均做题速度
        private Integer averageSpeed;
        // 平均正确率
        private Double averageCorrectRate;
        // 总做题数
        //private Integer totalCount;

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
            //this.totalCount = collection.size();
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

}
