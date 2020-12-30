package learn.light4j.domain.subscribe;

import com.alibaba.fastjson.JSON;
import com.networknt.utility.CollectionUtil;
import com.networknt.utility.StringUtils;
import learn.light4j.domain.EventTrackingMessage;
import learn.light4j.domain.subscribe.request.SubscribeCriteria;
import lombok.extern.slf4j.Slf4j;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSinkChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author: cao
 * @time: 2020/12/3 7:07 下午
 */
@Slf4j
public class SubscribeService {

    private static final AtomicInteger SUBSCRIBE_ID = new AtomicInteger(0);
    private static final Map<Integer, Subscriber> SUBSCRIBE_ID_MAP = new ConcurrentHashMap<>();
    private static final Map<String, List<StreamSinkChannel>> DATA_CENTER_ID_SUBSCRIBERS = new ConcurrentHashMap<>();
    private static final Map<String, List<StreamSinkChannel>> APP_USER_ID_SUBSCRIBERS = new ConcurrentHashMap<>();
    private static volatile SubscribeService INSTANCE;

    private SubscribeService(){}

    public static SubscribeService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            synchronized (SubscribeService.class) {
                if (Objects.isNull(INSTANCE)) {
                    INSTANCE = new SubscribeService();
                }
            }
        }
        return INSTANCE;
    }

    public Integer nextId() {
        return SUBSCRIBE_ID.incrementAndGet();
    }


    public void receiveMessage(List<EventTrackingMessage> messages) {

        int size = messages.size();
        log.debug("推数消费者收到{}条消息", size);
        messages.forEach(message -> {
            String dataCenterId = message.getDataCenterId();
            String appUserId = message.getApplicationId();
            Stream.of(APP_USER_ID_SUBSCRIBERS.getOrDefault(appUserId, Collections.emptyList()),
                    DATA_CENTER_ID_SUBSCRIBERS.getOrDefault(dataCenterId, Collections.emptyList()))
                    .filter(it -> !CollectionUtil.isEmpty(it))
                    .flatMap(List::stream).filter(Objects::nonNull).distinct()
                    .forEach(channel -> sendClientMessage(channel, message));
        });
    }




    public void addSubscriber(Subscriber subscriber) {
        SUBSCRIBE_ID_MAP.put(subscriber.getId(), subscriber);
        subscribe(subscriber.getCriteria(), subscriber.getChannel());
    }

    public void removeSubscriber(Integer id) {

        Subscriber subscriber = SUBSCRIBE_ID_MAP.get(id);
        if (Objects.isNull(subscriber)) {
            return;
        }

        //1.取消推数订阅
        cancelSubScribe(subscriber.getCriteria(), subscriber.getChannel());

        //2.释放阻塞
        IoUtils.safeClose(subscriber.getConnection());

        //3.订阅者释放
        SUBSCRIBE_ID_MAP.remove(id);
    }


    public boolean connected(Integer id) {
        return SUBSCRIBE_ID_MAP.containsKey(id);
    }

    private void subscribe(SubscribeCriteria criteria, StreamSinkChannel channel) {
        String dataCenterId = criteria.getDataCenterId();
        String appUserId = criteria.getAppUserId();
        if (Objects.isNull(channel)) {
            return;
        }
        if (StringUtils.isNotBlank(dataCenterId)) {
            doSubscribe(DATA_CENTER_ID_SUBSCRIBERS, dataCenterId, channel);
        }
        if (StringUtils.isNotBlank(appUserId)) {
            doSubscribe(APP_USER_ID_SUBSCRIBERS, appUserId, channel);
        }
        log.debug("{}订阅dataCenterId={}, appUserId={}", channel.hashCode(), dataCenterId, appUserId);
        log.debug("当前订阅者个数:{}", SUBSCRIBE_ID_MAP.size());
    }

    private void cancelSubScribe(SubscribeCriteria criteria, StreamSinkChannel channel) {

        if (Objects.isNull(channel)) {
            return;
        }
        String dataCenterId = criteria.getDataCenterId();
        String appUserId = criteria.getAppUserId();
        if (StringUtils.isNotBlank(dataCenterId)) {
            doCancelSubScribe(DATA_CENTER_ID_SUBSCRIBERS, dataCenterId, channel);
        }
        if (StringUtils.isNotBlank(appUserId)) {
            doCancelSubScribe(APP_USER_ID_SUBSCRIBERS, appUserId, channel);
        }
        log.debug("{}取消订阅dataCenterId={}, appUserId={}", channel.hashCode(), dataCenterId, appUserId);
    }


    private void doSubscribe(
            Map<String, List<StreamSinkChannel>> subscribers,
            String userId,  StreamSinkChannel channel) {
        List<StreamSinkChannel> channels = subscribers.getOrDefault(userId, new ArrayList<>());
        channels.add(channel);
        subscribers.put(userId, channels);
    }

    private void doCancelSubScribe(
            Map<String, List<StreamSinkChannel>> subscribers,
            String userId, StreamSinkChannel channel) {
        List<StreamSinkChannel> channels = subscribers.getOrDefault(userId, Collections.emptyList());
        channels.remove(channel);
        subscribers.put(userId, channels);
    }


    private void sendClientMessage(StreamSinkChannel channel, EventTrackingMessage message) {

        ByteBuffer contentBuffer = ByteBuffer.wrap(("data: " + JSON.toJSONString(message) + "\n\n").getBytes());

        try {
            if (channel.isOpen()) {
                channel.write(contentBuffer);
            }
        } catch (IOException ignored) { }
    }

}
