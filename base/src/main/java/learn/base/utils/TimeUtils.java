package learn.base.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 基于JDK8的时间相关工具类
 *
 * @author Zephyr
 * @since 2022-4/12.
 */
public class TimeUtils {
    // Javac编译器会对源代码做的极少量优化措施之一: 常量折叠
    public static final long MILLIS_PER_MINUTE = 60_000L;
    public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
    public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
    public static final long MILLIS_PER_WEEK = MILLIS_PER_DAY * 7;

    public static final ZoneOffset zone_offset = ZoneOffset.ofHours(8);

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>(8);
    static {
        FORMATTER_CACHE.put("yyyy-MM-dd", DATE_FORMATTER);
        FORMATTER_CACHE.put("HH:mm:ss", TIME_FORMATTER);
        FORMATTER_CACHE.put("yyyy-MM-dd HH:mm:ss", DATE_TIME_FORMATTER);
        FORMATTER_CACHE.put("yyyy-MM-ddTHH:mm:ss", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }


    /**
     * 时间戳 转 LocalDate
     */
    public static LocalDate toLocalDate(long timeMillis) {
        return Instant.ofEpochMilli(timeMillis).atZone(zone_offset).toLocalDate();
    }

    /**
     * 时间戳 转 LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long timeMillis) {
        return Instant.ofEpochMilli(timeMillis).atZone(zone_offset).toLocalDateTime();
    }

    /**
     * LocalDate 转 时间戳
     */
    public static long toTimestamp(LocalDate localDate, LocalTime localTime) {
        if (localTime == null) {
            localTime = LocalTime.MIN;
        }
        return localDate.atTime(localTime).toInstant(zone_offset).toEpochMilli();
    }

    /**
     * LocalDateTime 转 时间戳
     */
    public static long toTimestamp(LocalDateTime localDateTime) {
        return localDateTime.toInstant(zone_offset).toEpochMilli();
    }

    /**
     * 获取指定日期 一天开始时的午夜时间（'00:00'）所对应的时间戳
     * @param timeMillis 任意时间的时间戳，毫秒值
     */
    public static long getDateMinTime(long timeMillis) {
        LocalDate localDate = Instant.ofEpochMilli(timeMillis).atZone(zone_offset).toLocalDate();
        return localDate.atTime(LocalTime.MIN).toInstant(zone_offset).toEpochMilli();
    }

    public static long getDateMinTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.MIN).toInstant(zone_offset).toEpochMilli();
    }

    /**
     * 获取指定日期 一天结束时午夜之前的时间（'23:59:59.999'）所对应的时间戳,再加 1ms 即为第二天的时间
     * @param timeMillis 任意时间的时间戳，毫秒值
     */
    public static long getDateMaxTime(long timeMillis) {
        LocalDate localDate = Instant.ofEpochMilli(timeMillis).atZone(zone_offset).toLocalDate();
        return localDate.atTime(LocalTime.MAX).toInstant(zone_offset).toEpochMilli();
    }

    public static long getDateMaxTime(LocalDate localDate) {
        return localDate.atTime(LocalTime.MAX).toInstant(zone_offset).toEpochMilli();
    }

    /**
     * 时间戳 转 日期格式的字符串
     */
    public static String toDateStr(long timeMillis) {
        return Instant.ofEpochMilli(timeMillis).atZone(zone_offset).toLocalDate().toString();
    }

    /**
     * 时间戳 转 日期时间(时分秒)格式的字符串
     */
    public static String toDateTimeStr(long timeMillis) {
        return DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(timeMillis).atZone(zone_offset).toLocalDateTime());
    }

    /**
     * 时间戳 转 日期时间(时分秒)格式的字符串
     */
    public static String toDateTimeStr(long timeMillis, String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, pa -> DateTimeFormatter.ofPattern(pa, Locale.CHINA))
            .format(Instant.ofEpochMilli(timeMillis).atZone(zone_offset).toLocalDateTime());
    }

    /**
     * 根据指定日期格式解析字符串,请注意验证格式是否合法
     */
    public static LocalDate parseDate(String str, String pattern) {
        return LocalDate.parse(str, FORMATTER_CACHE.computeIfAbsent(pattern, pa -> DateTimeFormatter.ofPattern(pa, Locale.CHINA)));
    }

    /**
     * 根据指定时间格式解析字符串,请注意验证格式是否合法
     */
    public static LocalTime parseTime(String str, String pattern) {
        return LocalTime.parse(str, FORMATTER_CACHE.computeIfAbsent(pattern, pa -> DateTimeFormatter.ofPattern(pa, Locale.CHINA)));
    }

    /**
     * 根据指定日期时间格式解析字符串,请注意验证格式是否合法
     */
    public static LocalDateTime parseDateTime(String str, String pattern) {
        return LocalDateTime.parse(str, FORMATTER_CACHE.computeIfAbsent(pattern, pa -> DateTimeFormatter.ofPattern(pa, Locale.CHINA)));
    }

    public static DateTimeFormatter getDateTimeFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, pa -> DateTimeFormatter.ofPattern(pa, Locale.CHINA));
    }


    /**
     * 判断时间 time 是否在指定时间段内(闭区间)
     */
    public static boolean isTimeBetween(Long time, Long startTime, Long endTime) {
        return Boolean.logicalAnd(startTime == null || time >= startTime, endTime == null || time <= endTime);
    }
}
