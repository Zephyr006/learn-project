package learn.light4j.handler;

import com.alibaba.fastjson.JSON;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import learn.light4j.domain.EventTrackingMessage;
import learn.light4j.provider.KafkaStartupHookProvider;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Random;

/**
 * @author: cao
 * @time: 2020/12/10 1:59 下午
 */
public class MessageSendHandler implements HttpHandler {


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                KafkaStartupHookProvider.KAFKA_CONFIG.getTopic(),
                JSON.toJSONString(getMessage(System.currentTimeMillis())).getBytes());
        KafkaStartupHookProvider.producer.send(record);

        exchange.getResponseSender().send("{\"success\":\"true\"}");
    }


    public EventTrackingMessage getMessage(Long timestamp) {
        return EventTrackingMessage.builder()
                .deviceId(String.valueOf(new Random().nextInt(10000)))
                .deviceType("driverType")
                .spanId((int)(Math.random() * 100) + "")
                .pageId((int)(Math.random() * 100 )+ "")
                .testGroup("A").operationId((int)(Math.random() * 200) + "")
                .dataCenterId((int)(Math.random() * 200) + "").data("message data...")
                .version("7.13")
                .applicationId((int)(Math.random() * 200) + "").timestamp(timestamp)
                .build();
    }
}
