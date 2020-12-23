package learn.light4j.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.networknt.body.BodyHandler;
import com.networknt.handler.LightHttpHandler;
import learn.light4j.constants.AttachmentConstants;
import learn.light4j.constants.ErrorEnum;
import learn.light4j.util.CurrentTimeMillisClock;
import learn.light4j.util.KafkaUtil;
import learn.light4j.util.Results;
import io.undertow.server.HttpServerExchange;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * 将收到的埋点数据发送到 kafka
 *
 * @author Zephyr
 * @date 2020/12/3.
 */
public class KafkaProducerHandler implements LightHttpHandler {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducerHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private static String SUCCESS_RESULT_STRING;
    private static final String TIMESTAMP = "timestamp";
    private static final String logKafkaResult = System.getProperty("log.kafka", "n");

    static {
        try {
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            SUCCESS_RESULT_STRING = mapper.writeValueAsString(Results.success(null));
        } catch (JsonProcessingException ignore) { }
    }


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        Map<String, Object> requestBody = (Map<String, Object>) exchange.getAttachment(BodyHandler.REQUEST_BODY);
        Map<String, Object> idAndDataCenterIdMap = exchange.getAttachment(AttachmentConstants.USER_INFO);

        if (requestBody == null || requestBody.isEmpty()) {
            exchange.setStatusCode(200);
            exchange.getResponseSender().send(mapper.writeValueAsString(Results.fail(ErrorEnum.LACK_PARAM)));
            return;
        }
        requestBody.putAll(idAndDataCenterIdMap);
        requestBody.put(TIMESTAMP, CurrentTimeMillisClock.now());

        // send to kafka
        Future<RecordMetadata> metadataFuture = KafkaUtil.send(mapper.writeValueAsString(requestBody));
        if ("y".equals(logKafkaResult) || "yes".equals(logKafkaResult)) {
            RecordMetadata recordMetadata = metadataFuture.get();
            log.info("kafka message send success, topicPartition = {}", recordMetadata.toString());
        }
        exchange.setStatusCode(200);
        exchange.getResponseSender().send(SUCCESS_RESULT_STRING);
    }

}
