package learn.light4j.domain.timeperiod;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimePeriodTranslator implements TimePeriodTranslator {

    public static final ThreadLocal<DateFormat> SAFE_SIMPLE_DATE_FORMAT =
            ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    @Override
    public String translate(Long millsTimestamp) {
        return SAFE_SIMPLE_DATE_FORMAT.get().format(new Date(millsTimestamp));
    }
}
