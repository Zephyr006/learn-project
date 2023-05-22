package learn.simple.datasource.question.entity;

import lombok.Data;

/**
 * @author Zephyr
 * @date 2021/1/12.
 */
@Data
public class QuestionTag {


    private Long questionId;

    private Long tagId;

    private Long createdAt;

    private Long updatedAt;

    private Boolean status;
}
