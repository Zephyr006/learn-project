package learn.base.test.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zephyr
 * @date 2021/5/6.
 */
@Getter
@Setter
public class UserQuestionLog {
    public static final Map<Integer, Integer> SCENE_WEIGHT = new HashMap<>();
    static {
        SCENE_WEIGHT.put(1, 1);  //10
        SCENE_WEIGHT.put(2, 1);  //7
        SCENE_WEIGHT.put(3, 1);  //2
        SCENE_WEIGHT.put(4, 1);  //7
        SCENE_WEIGHT.put(5, 1);  //5
    }

    //private Long id;
    private Long userId;
    private Long questionId;
    private Long submitId;
    private Boolean correct;
    private Integer costTime;
    private Integer scenesKey;

    @Getter
    @Setter
    public static class SubmitLog {
        private Long id;
        private Long userId;
        private Integer scenesKey;

    }

    /**
     * select * from user_question_log_* where user_id in (%s) and question_id in (%s) and cost_time > 300 and status = 1
     */
}
