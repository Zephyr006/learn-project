package learn.datasource.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: caoyanan
 * @time: 2021/1/14 5:18 下午
 */
@Getter
@AllArgsConstructor
public enum DataServerErrorEnum  {


    /**
     * relation
     */
    USER_ID_NOT_EXISTS("gaea_crm_dataserver_01", "用户id不存在"),
    ;

    private final String code;
    private final String message;

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
