package learn.datasource.entity.relation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author: caoyanan
 * @time: 2021/1/15 6:37 下午
 */
@Data
public class SubmitLog {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scenesKey;
    private Long scenesId;
    private Long userId;
    private String metadata;
    private Long genericKey;

    private Long createdAt;
    private Long updatedAt;
    private Boolean status;
}
