package learn.simple.datasource.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author Zephyr
 * @date 2021/1/12.
 */
@Data
public class QuestionKnowledge {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionId;

    private Long knowledgeId;

    private Long createdAt;

    private Long updatedAt;

    private Boolean status;
}
