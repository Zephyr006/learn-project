package learn.datasource.entity.gk;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class UserQuestionLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long questionId;
    private Long submitId;
    private String answer;
    private Boolean correct;
    private Boolean errorMark;
    private Integer costTime;

    private Long createdAt;
    private Long updatedAt;
    private Boolean status;
}
