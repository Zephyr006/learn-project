package learn.light4j.model;

import learn.base.utils.JsonUtil;
import lombok.Builder;
import lombok.Data;

/**
 * @author: caoyanan
 * @time: 2020/12/8 7:04 下午
 */
@Builder
@Data
public class BaseResponse<T> {

    private Boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> String ok(T t) {
        return JsonUtil.toJSONString(BaseResponse.builder()
                .success(true).code("10000")
                .message("操作成功").data(t).build());
    }
    public static <T> String ok() {
        return ok(null);
    }

    public static String fail(ResponseCode responseCode) {
        return fail(responseCode.getCode(), responseCode.getMessage());
    }

    public static String fail(String code, String message) {
        return JsonUtil.toJSONString(BaseResponse.builder()
                .success(false).code(code)
                .message(message).build());
    }
}
