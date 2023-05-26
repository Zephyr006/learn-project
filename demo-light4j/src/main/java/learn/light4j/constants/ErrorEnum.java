package learn.light4j.constants;

import lombok.Getter;

/**
 * @author Zephyr
 * @since 2020-12-03.
 */
@Getter
public enum ErrorEnum {

    INNER_ERROR("FAIL_0501", "内部异常"),
    LACK_PARAM("FAIL_0502", "必填参数为空，或未正确解析到参数值"),
    ;


    private String code;
    private String message;

    ErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
