package learn.springcloud.config.client.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * @author Zephyr
 * @date 2021/1/11.
 */
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class JkUser extends User{

    @TableId(type = IdType.INPUT)
    private Long id;
    //private String phone;
    //
    //private Long dataCenterId;
    //private Timestamp createTime;
    //private Timestamp updateTime;
    //
    //private String latestRequestTime;
    //private Integer status;
    //private Integer activeTimesOneMonth;


    public JkUser(Long id, Integer activeTimesBothRegister) {
        this.id = id;
        this.activeTimesBothRegister = activeTimesBothRegister;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JkUser user = (JkUser) o;
        return id.equals(user.id) &&
                phone.equals(user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, phone);
    }
}
