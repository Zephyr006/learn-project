package learn.simple.datasource.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Tag {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属标签树
     */
    private Long treeId;

    /**
     * 父级标签
     */
    private Long parentId;


    /**
     * 标签名称
     */
    private String name;

    /**
     * 1：题目类型
     * 2：题目生产类型
     */
    private Integer type;

    @TableField("`index`")
    private Integer index;

    private Long createdAt;

    private Long updatedAt;

    private Boolean status;
}
