package learn.springboot.datasource.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author Zephyr
 * @since 2021-1-12.
 */
@Data
public class TagTree {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String name;

    /**
     * 1：题目类型
     * 2：题目生产类型
     */
    private Integer type;


    private Long createdAt;

    private Long updatedAt;

    private Boolean status;
}
