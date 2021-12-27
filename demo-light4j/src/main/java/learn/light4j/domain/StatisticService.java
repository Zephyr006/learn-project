package learn.light4j.domain;

import com.networknt.utility.CollectionUtil;
import learn.light4j.domain.entity.StatisticEntity;
import learn.light4j.domain.entity.StatisticQueryCriteria;
import learn.light4j.domain.timeperiod.StatisticEventCount;
import learn.light4j.domain.timeperiod.StatisticTimeEvent;
import learn.light4j.domain.timeperiod.TimePeriodTranslator;
import learn.light4j.domain.timeperiod.TimePeriodType;
import learn.light4j.model.BaseResponse;
import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class StatisticService {

    private static volatile StatisticService INSTANCE;

    /**
     * 统计map
     */
    private Map<Long, Map<Integer, AtomicInteger>> timeAndCountMap = new ConcurrentHashMap<>();

    /**
     * 统计数据落库间隔
     */
    public static final Long SYNC_INTERVAL_MILLIS = 10000L;

    /**
     * 上次同步数据库时间
     */
    private Long lastSyncTime;

    private StatisticManager statisticManager = StatisticManager.getInstance();

    private StatisticService(){
        lastSyncTime = System.currentTimeMillis();
    }

    public static StatisticService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (StatisticService.class) {
                if (Objects.isNull(INSTANCE)) {
                    INSTANCE = new StatisticService();
                }
            }
        }
        return INSTANCE;
    }


    public void receiverMessage(List<EventTrackingMessage> messages) {

        log.debug("计数消费者收到{}条消息", messages.size());

        updateStatisticTime();

        messages.forEach(message -> {
            String operationId = message.getOperationId();
            Integer eventType = null;
            try {
                eventType = Integer.parseInt(operationId);
            } catch (Exception ignored) {}

            if (Objects.isNull(eventType)) {
                return;
            }

            LocalDate localDate = Instant.ofEpochMilli(message.getTimestamp())
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            long zeroTime = LocalDateTime.of(localDate, LocalTime.MIN)
                    .toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
            timeAndCountMap.computeIfAbsent(zeroTime, v -> new ConcurrentHashMap<>())
                    .computeIfAbsent(eventType, v -> new AtomicInteger(0))
                    .incrementAndGet();
        });
    }

    public String queryStatisticCounts(StatisticQueryCriteria criteria) {

        List<StatisticEntity> statisticEntities = statisticManager
                .findByCriteria(criteria.getStartTime(), criteria.getEndTime(), criteria.getEventTypes());
        List<String> events = statisticEntities.stream()
                .filter(it -> Objects.nonNull(it.getEventType()))
                .map(it -> it.getEventType() + "")
                .distinct().collect(Collectors.toList());

        TimePeriodType timePeriodType = TimePeriodType.valueOf(criteria.getTimePeriod());
        TimePeriodTranslator translator = timePeriodType.getTranslator();
        Map<String, List<StatisticEntity>> timeAndStatisticsMap = statisticEntities.stream()
                .collect(Collectors.groupingBy(it -> translator.translate(it.getStatisticTime())));

        List<StatisticTimeEvent> timeEvents = timeAndStatisticsMap.entrySet().stream()
                .map(entry -> {
                    String time = entry.getKey();
                    List<StatisticEntity> statistics = entry.getValue();
                    List<StatisticEventCount> eventCounts = statistics.stream()
                            .collect(Collectors.groupingBy(StatisticEntity::getEventType))
                            .entrySet().stream().map(eventTypeCountEntry -> {
                                int counts = eventTypeCountEntry.getValue().stream()
                                        .mapToInt(StatisticEntity::getCount).sum();
                                return new StatisticEventCount(
                                        eventTypeCountEntry.getKey() + "", counts);
                            }).collect(Collectors.toList());
                    return new StatisticTimeEvent(time, eventCounts);
                }).sorted(Comparator.comparing(StatisticTimeEvent::getTime))
                .collect(Collectors.toList());

        //return new BaseResponse(events, timeEvents);
        return BaseResponse.ok(timeEvents);
    }


    /**
     * 提取出一段时间内的所有日期
     * @param startTime
     * @param endTime
     * @return
     */
    private List<String> extractDays(Long startTime, Long endTime) {
        List<String> days = new ArrayList<>();
        LocalDateTime localDateTime = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(startTime),
                        ZoneId.systemDefault());
        LocalDateTime endLocalDateTime = LocalDateTime
                .ofInstant(Instant.ofEpochMilli(endTime),
                        ZoneId.systemDefault());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (localDateTime.isBefore(endLocalDateTime)
                || localDateTime.isEqual(endLocalDateTime)) {
            LocalDate localDate = localDateTime.toLocalDate();
            days.add(localDate.format(df));
            localDateTime = localDateTime.plusDays(1L);
        }
        return days;
    }



    /**
     * 更新统计时间
     */
    private void updateStatisticTime() {

        long now = System.currentTimeMillis();
        if (now - lastSyncTime > SYNC_INTERVAL_MILLIS) {
            synchronized (StatisticService.class) {
                if (now - lastSyncTime > SYNC_INTERVAL_MILLIS) {
                    persistence();
                    timeAndCountMap = new ConcurrentHashMap<>();
                }
            }
        }
    }


    /**
     * 统计持久化到数据库
     */
    private void persistence() {
        long now = System.currentTimeMillis();
        List<StatisticEntity> statisticCountEntities = timeAndCountMap.entrySet()
                .stream().flatMap(entry -> entry.getValue().entrySet()
                        .stream().map(eventEntry -> StatisticEntity.builder()
                                .eventType(eventEntry.getKey())
                                .count(eventEntry.getValue().get())
                                .statisticTime(entry.getKey())
                                .updatedAt(now).build()))
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(statisticCountEntities)) {
            return;
        }
        Integer count = statisticManager.batchInsertOrUpdate(statisticCountEntities);
        log.info("同步数据库{}条记录", count);
        lastSyncTime = now;
    }
}
