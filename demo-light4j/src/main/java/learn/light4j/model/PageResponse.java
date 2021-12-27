package learn.light4j.model;

import learn.base.utils.JsonUtil;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PageResponse<T> {

    private Long totalCount;
    private Boolean success;
    private String code;
    private String message;
    private List<T> data;

    public static <T> String ok(List<T> t, Long totalCount) {
        return JsonUtil.toJSONString(PageResponse.<T>builder()
                .success(true).code("100000")
                .message("操作成功")
                .data(t).totalCount(totalCount).build());
    }
}
