package learn.springboot.datasource.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`group`")
public class Group {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    @TableField("`desc`")
    private String desc;

    private Long documentId;

    private long createdAt;

    private long updatedAt;

    private Boolean status;
}
