package learn.springboot.datasource.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Question {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer type;

    private String questionKind;

    private Boolean hasImg;

    private String segment;

    private Boolean isSubjective;

    private Integer version;

    private long createdAt;

    private long updatedAt;

    private Boolean status;
}
