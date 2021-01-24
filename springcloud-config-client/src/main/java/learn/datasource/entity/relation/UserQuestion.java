package learn.datasource.entity.relation;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author: caoyanan
 * @time: 2021/1/18 1:57 下午
 */
@Data
public class UserQuestion {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long questionId;
    private String stats;
    private Integer answerCount;
    private Integer sumCostTime;
    private Boolean isFavorite;
    private Integer correctCount;
    private String lastResult;
    private Integer lastCostTime;
    private Integer consequence;

    private Long createdAt;
    private Long updatedAt;
    private Boolean status;
}
