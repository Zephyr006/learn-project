package learn.light4j.handler;

import com.networknt.utility.CollectionUtil;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import learn.light4j.domain.subscribe.SubscribeService;
import learn.light4j.domain.subscribe.Subscriber;
import learn.light4j.domain.subscribe.request.SubscribeCriteria;
import lombok.extern.slf4j.Slf4j;
import org.xnio.channels.StreamSinkChannel;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;


/**
 * @author: cao
 * @time: 2020-12-2 6:18 下午
 */
@Slf4j
public class SubscribeMessageHandler implements HttpHandler {

    public static final String SUBSCRIBE_URL = "/api/subscribe";


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        StreamSinkChannel channel = exchange.getResponseChannel();

        SubscribeCriteria criteria = extractParam(exchange);

        //订阅
        ServerConnection connection = exchange.getConnection();
        SubscribeService subscribePool = SubscribeService.getInstance();
        Integer id = subscribePool.nextId();
        Subscriber subscriber = Subscriber.builder()
                .id(id).channel(channel)
                .connection(connection)
                .criteria(criteria).build();
        subscribePool.addSubscriber(subscriber);

        channel.write(ByteBuffer.wrap(("id: "+id+"\nevent: id\ndata: connected success\n\n").getBytes(StandardCharsets.UTF_8)));

        //保持连接
        exchange.dispatch(Runnable::run, () -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (!channel.isOpen()) {
                        break;
                    }
                    channel.write(ByteBuffer.wrap(("id: "+id+"\nevent: heartbeat\n\n").getBytes(StandardCharsets.UTF_8)));
                } catch (Exception e) {
                    log.error("监控断开连接线程异常", e);
                }
            }
            subscribePool.removeSubscriber(id);
            log.info("连接断开:{}", channel.hashCode());
        });
    }


    private SubscribeCriteria extractParam(HttpServerExchange exchange) {
        SubscribeCriteria criteria = new SubscribeCriteria();
        Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
        Deque<String> dataCenterIdDeque = queryParameters.getOrDefault("dataCenterId", new ArrayDeque<>());
        if (!CollectionUtil.isEmpty(dataCenterIdDeque)) {
            criteria.setDataCenterId(dataCenterIdDeque.getFirst());
        }

        Deque<String> appUserIdDeque = queryParameters.getOrDefault("appUserId", new ArrayDeque<>());
        if (!CollectionUtil.isEmpty(appUserIdDeque)) {
            criteria.setAppUserId(appUserIdDeque.getFirst());
        }
        return criteria;
    }
}
