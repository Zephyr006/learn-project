package learn.light4j.domain.timeperiod;


public interface TimePeriodTranslator {

    /**
     * 转换毫秒时间戳
     * @param millsTimestamp
     * @return
     */
    String translate(Long millsTimestamp);
}
