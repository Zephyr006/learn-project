package learn.datasource.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author: caoyanan
 * @time: 2021/1/18 4:30 下午
 */
@AllArgsConstructor
@Getter
public enum QuestionType {

    /**
     * 单选
     */
    SELECT(1),

    /**
     * 多选
     */
    MULTIPLE_SELECTION(2),

    /**
     * 主观
     */
    SUBJECTIVE(3),

    /**
     * 连线
     */
    CONNECT(4),
    ;

    private final Integer value;

    @JsonValue
    public Integer getValue() {
        return this.value;
    }

    @JsonCreator
    public static QuestionType create(String name) {
        if (Objects.isNull(name) || "".equals(name)) {
            return null;
        }
        return valueOf(Integer.parseInt(name));
    }

    public static QuestionType valueOf(Integer value) {
        for (QuestionType valueEnum : values()) {
            if (value.equals(valueEnum.value)) {
                return valueEnum;
            }
        }
        throw new IllegalArgumentException("illegal enum value");
    }
}
