package learn.datasource.entity.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author caoyanan
 * @date 2021/01/14
 */
@Data
public class KnowledgeRoot {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long createdAt;

    private Long updatedAt;

    private Boolean status;
}
