package learn.datasource.entity.gk;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Zephyr
 * @date 2021/1/21.
 */
@Data
@TableName("raw_account")
public class GkAccount implements Serializable {

    @TableId
    private Long id;
    private String phone;

    private Long dataCenterId;
    private Timestamp createTime;
    private Timestamp updateTime;

    private String latestRequestTime;
    private Integer status;

}
