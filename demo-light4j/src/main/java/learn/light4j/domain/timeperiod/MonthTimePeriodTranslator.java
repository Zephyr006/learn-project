package learn.light4j.domain.timeperiod;

import java.util.Calendar;

public class MonthTimePeriodTranslator implements TimePeriodTranslator {

    public final Calendar CALENDAR = Calendar.getInstance();

    //static {
    //    CALENDAR.setFirstDayOfWeek(Calendar.MONDAY);
    //}

    @Override
    public String translate(Long millsTimestamp) {
        CALENDAR.setFirstDayOfWeek(Calendar.MONDAY);
        CALENDAR.setTimeInMillis(millsTimestamp);

        return String.format("%s-%s",
                CALENDAR.get(Calendar.YEAR),
                CALENDAR.get(Calendar.MONTH) + 1);
    }

}
