package learn.datasource.entity;

import lombok.Data;

/**
 * @author yangming
 * @date 2020/12/2
 */
@Data
public class UserLoginInfo {

    private Long dataCenterId;

    /**
     * 上次登录时间
     */
    private Long latestRequestTime;

    /**
     * 登陆次数
     */
    private Integer loginCount;

    /**
     * 注册时间
     */
    private Long createTime;
}
