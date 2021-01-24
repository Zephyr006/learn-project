package learn.datasource.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author: caoyanan
 * @time: 2020/10/29 6:17 下午
 */
@TableName("orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderEntity {

    /**
     * 中台用户id
     */
    private Long dataCenterId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 商品名称
     */
    private String subject;

    /**
     * 付款金额(分)
     */
    private Integer price;

    /**
     * 付款时间
     */
    private Date payTime;

    /**
     * 状态(1:付款，3：退款)
     */
    private Integer status;
}
