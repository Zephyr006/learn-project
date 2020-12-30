package learn.light4j.domain.timeperiod;

import java.util.Calendar;

/**
 * @author: caoyanan
 * @time: 2020/12/9 3:27 下午
 */
public class WeekTimePeriodTranslator implements TimePeriodTranslator {

    public static final Calendar CALENDAR = Calendar.getInstance();
    static {
        CALENDAR.setFirstDayOfWeek(Calendar.MONDAY);
    }

    @Override
    public String translate(Long millsTimestamp) {

        CALENDAR.setTimeInMillis(millsTimestamp);

        return String.format("%s年%s月第%s周",
                CALENDAR.get(Calendar.YEAR), CALENDAR.get(Calendar.MONTH) + 1,
                CALENDAR.get(Calendar.WEEK_OF_MONTH));
    }
}
