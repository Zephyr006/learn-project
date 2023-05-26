package learn.light4j.util;

import learn.light4j.constants.ErrorEnum;
import learn.light4j.model.Result;

/**
 * @author Zephyr
 * @since 2020-12-2.
 */
public class Results {

    public static final String success_code = "SUCC_0001";
    public static final String success_msg = "操作成功";


    public static Result success(Object data) {
        return Result.of(success_code, success_msg, data);
    }

    public static Result fail(String code, String message) {
        return Result.of(code, message, null);
    }

    public static Result fail(ErrorEnum errorEnum) {
        return Result.of(errorEnum.getCode(), errorEnum.getMessage(), null);
    }


}
