package learn.simple.datasource.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Knowledge {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long parentId;

    private Long rootId;

    private Integer sort;

    private long createdAt;

    private long updatedAt;

    private Boolean status;
}
