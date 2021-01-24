package learn.datasource.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author: caoyanan
 * @time: 2021/1/15 3:58 下午
 */
@TableName("homeworks")
@Data
public class HomeworkEntity {

    private Long id;
    private String name;

    /**
     * 关联题组id
     */
    private Long questionGroupId;

    /**
     * 状态, 0正常，1删除
     */
    private Integer status;
}
