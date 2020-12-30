package learn.light4j.domain.timeperiod;

/**
 * 时间周期转换器
 * @author: caoyanan
 * @time: 2020/12/9 3:16 下午
 */
public interface TimePeriodTranslator {

    /**
     * 转换毫秒时间戳
     * @param millsTimestamp
     * @return
     */
    String translate(Long millsTimestamp);
}
