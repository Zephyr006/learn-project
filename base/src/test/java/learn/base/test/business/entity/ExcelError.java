package learn.base.test.business.entity;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Zephyr
 * @since 2021-4-17.
 */
@Getter
@ToString
public class ExcelError {

    public static final String MSG_CAN_NOT_BE_EMPTY = "单元格的值不能为空";
    public static final String MSG_COMMON_ERROR = "单元格内容填写错误";

    private String address;
    private String errorMsg;

    public ExcelError(String errorMsg, String... address) {
        assert address != null;

        this.errorMsg = errorMsg;
        if (address.length > 1) {
            this.address = StringUtils.join(address, ",");
        }
        this.address = address[0];
    }
}
