package learn.light4j.model;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2020/12/22 4:18 下午
 */
@Builder
@Data
public class PageResponse<T> {

    private Long totalCount;
    private Boolean success;
    private String code;
    private String message;
    private List<T> data;

    public static <T> String ok(List<T> t, Long totalCount) {
        return JSON.toJSONString(PageResponse.<T>builder()
                .success(true).code("100000")
                .message("操作成功")
                .data(t).totalCount(totalCount).build());
    }
}
