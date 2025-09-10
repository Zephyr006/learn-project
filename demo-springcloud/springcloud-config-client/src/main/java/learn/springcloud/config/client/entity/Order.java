package learn.springcloud.config.client.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Order {
    @TableId
    private Long id;
    private Long pid;
    private Long userId;
    private Integer amount;
}
