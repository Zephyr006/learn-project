package learn.base.test;

import learn.base.utils.FileLoader;
import learn.base.utils.KafkaUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Zephyr
 * @date 2021/3/7.
 */
public class KafkaConnectTest {
    private static final String FILE_PATH = "conn-test.properties";
    private static final String BOOTSTRAP_SERVERS_CONFIG_KEY = "kafka.server";
    private static final String KAFKA_TOPIC_KEY = "kafka.topic";


    @Test
    public void testConnect() throws ExecutionException, InterruptedException {
        final String topic = "kafka_connect_test";


        final Properties props = FileLoader.loadProperties(FILE_PATH);
        String host = String.valueOf(props.get("host"));
        System.setProperty(BOOTSTRAP_SERVERS_CONFIG_KEY, host + ":10003");
        System.setProperty(KAFKA_TOPIC_KEY, topic);

        Future<RecordMetadata> sendResultFuture = KafkaUtil.send(
                System.getProperty("kafka.topic"), "这是一条用于测试kafka连接可用性的消息！", KafkaUtil.DEFAULT_CALLBACK);
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
