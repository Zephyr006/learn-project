package learn.datasource.model;

import lombok.Data;

/**
 * @author yangming
 * @date 2020/12/2
 */
@Data
public class UserLoginCountModel {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 登陆次数
     */
    private Integer loginCount;
}
