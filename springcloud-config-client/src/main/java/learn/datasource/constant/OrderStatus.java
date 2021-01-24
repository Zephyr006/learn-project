package learn.datasource.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author yangming
 * @date 2020/10/30
 */
@AllArgsConstructor
@Getter
public enum OrderStatus {
    /**
     * 支付
     */
    PAYED(1),

    /**
     * 退款
     */
    REFUND(3),

    ;

    private final Integer value;

    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    @JsonCreator
    public static OrderStatus create(String name) {
        if (Objects.isNull(name) || "".equals(name)) {
            return null;
        }
        return valueOf(Integer.parseInt(name));
    }

    public static OrderStatus valueOf(Integer value) {
        for (OrderStatus valueEnum : values()) {
            if (value.equals(valueEnum.value)) {
                return valueEnum;
            }
        }
        throw new IllegalArgumentException("illegal enum value");
    }
}
