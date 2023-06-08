package learn.springboot.datasource.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class KnowledgeRoot {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long createdAt;

    private Long updatedAt;

    private Boolean status;
}
