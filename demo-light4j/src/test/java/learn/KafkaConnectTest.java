package learn;

import learn.light4j.util.KafkaUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Zephyr
 * @date 2021/3/7.
 */
public class KafkaConnectTest {
    private static final String BOOTSTRAP_SERVERS_CONFIG_KEY = "kafka.server";
    private static final String KAFKA_TOPIC_KEY = "kafka.topic";

    @Test
    public void testConnect() throws ExecutionException, InterruptedException {
        System.setProperty(BOOTSTRAP_SERVERS_CONFIG_KEY, "192.168.2.136:9092");
        System.setProperty(KAFKA_TOPIC_KEY, "kafka_connect_test");
        //System.out.println(System.getProperty(BOOTSTRAP_TOPIC_KEY));
        //System.out.println(System.getProperty(BOOTSTRAP_SERVERS_CONFIG_KEY));


        Future<RecordMetadata> sendResultFuture = KafkaUtil.send("这是一条用于测试kafka连接可用性的消息！");
        RecordMetadata recordMetadata = sendResultFuture.get();
        System.out.println(recordMetadata.toString());
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp() && recordMetadata.hasOffset());

        System.out.println("--- Kafka connect successfully! ---");

        while(true){
            ConsumerRecords<String, String> records = KafkaUtil.poll(Duration.ofSeconds(5));
            if (records.count() > 0) {
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record.value());
                }
                System.out.println("--- Kafka consume message successfully! ---");
                System.exit(0);
                //messageConsumer.accept(new ArrayList<>());
            }
        }
    }
}
