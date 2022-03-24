package learn.light4j.model;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BaseResponse<T> {

    private Boolean success;
    private String code;
    private String message;
    private T data;

    public static <T> String ok(T t) {
        return JSON.toJSONString(BaseResponse.builder()
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
        return JSON.toJSONString(BaseResponse.builder()
                .success(false).code(code)
                .message(message).build());
    }
}
