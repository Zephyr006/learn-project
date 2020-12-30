package learn.light4j.domain.timeperiod;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: caoyanan
 * @time: 2020/12/9 3:12 下午
 */
@AllArgsConstructor
@Getter
public enum TimePeriodType {
    /**
     * 日期
     */
    DATE(1, new DateTimePeriodTranslator()),

    /**
     * 周
     */
    WEEK(2, new WeekTimePeriodTranslator()),

    /**
     * 月份
     */
    MONTH(3, new MonthTimePeriodTranslator());
    ;

    private final Integer type;
    private final TimePeriodTranslator translator;

    @JsonCreator
    public static TimePeriodType valueOf(int i) {
        for (TimePeriodType value : values()) {
            if (value.type == i) {
                return value;
            }
        }
        throw new IllegalArgumentException("illegal time period value");
    }
}
