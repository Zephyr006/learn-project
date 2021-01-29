package learn.springcloud.config.client.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * @author Zephyr
 * @date 2021/1/11.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class User {

    Long id;
    String phone;

    Long dataCenterId;
    Timestamp createTime;
    Timestamp updateTime;

    String latestRequestTime;
    Integer status;

    Integer activeTimesOneMonth;
    Integer activeTimesBothRegister;


}
