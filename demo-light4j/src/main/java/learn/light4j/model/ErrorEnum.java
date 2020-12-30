package learn.light4j.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: caoyanan
 * @time: 2020/12/8 7:07 下午
 */
@Getter
@AllArgsConstructor
public enum ErrorEnum implements ResponseCode {


    /**
     * 统计接口
     */
    STATISTIC_OUT_OF_LIMIT("140001", "查询数据超出限制"),
    STATISTIC_EVENT_TYPE_ILLEGAL("140002", "埋点类型不可为空"),
    STATISTIC_TIME_PERIOD_ILLEGAL("140003","时间周期不可为空"),

    /**
     * 取消订阅接口
     */
    CANCEL_SUBSCRIBE_ID_NOT_NULL("150001", "id不可为空"),

    /**
     * 登录接口
     */
    USER_NAME_CAN_NOT_BLACK("160001", "用户名不可为空"),
    PASSWORD_CAN_NOT_BLACK("160001", "密码不可为空"),
    ILLEGAL_USER("160002", "用户名或密码错误"),
    REFRESH_TOKEN_NOT_NULL("160003", "refresh token 不可为空"),

    /**
     * 埋点类型
     */
    OPERATION_ID_ALREADY_EXISTS("170001", "埋点id已存在"),

    ;

    private final String code;
    private final String message;
}
