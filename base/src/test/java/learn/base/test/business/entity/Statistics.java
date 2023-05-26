package learn.base.test.business.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * @author Zephyr
 * @since 2021-4-30.
 */
@Getter
@Builder
@AllArgsConstructor
public class Statistics implements Serializable, Comparable<Statistics> {
    public static String PRINT_TEMPLATE = "档位%s , 平均正确率为 %.2f%%  ， 平均每道题的答题速度为 %d 秒 ，对应总用户数 %d 人";

    static int separate = UserDataConfig.levelSeparate;
    private String name;
    private Integer level;
    private Double correctRate;
    private Integer speed;  // 单位：毫秒
    private Integer count;

    //public Statistics(String name, Integer level, Double correctRate, Integer speed, Integer count) {
    //    this.name = name;
    //    this.level = level;
    //    this.correctRate = correctRate * 100;
    //    this.speed = speed;
    //    this.count = count;
    //}

    public static Statistics EMPTY = new Statistics("", 0, 0d, 0, 0);

    public String getPercentDesc() {
        int i = level * separate;
        if (i == 100) {
            return "100% - 100%";
        } else {
            return String.format("%d%% - %d%%", i, (level + 1) * separate);
        }
    }

    public String getCorrectRateDesc() {
        if (correctRate < 0) {
            return "-";
        }
        if (correctRate == 0) {
            return "0%";
        }
        return new DecimalFormat("#0.00%").format(correctRate);
    }

    public Integer getSpeedSecond() {
        return speed / 1000;
    }

    public String toFormatString() {
        return String.format(PRINT_TEMPLATE, level, correctRate, getSpeedSecond(), count);
    }

    @Override
    public int compareTo(final Statistics o) {
        return this.correctRate.compareTo(o.correctRate);
    }

    public void setLevel(final Integer level) {
        this.level = level;
    }
}
