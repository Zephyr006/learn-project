package learn.datasource.entity.jk;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author: caoyanan
 * @time: 2021/1/14 6:30 下午
 */
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
