package learn.light4j.util;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author Zephyr
 * @date 2020/11/19
 */
public class KafkaUtil {
    public static final Logger logger = LoggerFactory.getLogger(KafkaUtil.class);
    private static KafkaProducer<String, String> producer;
    private static String topic = null;

    static {
        init();
    }

    public static void init() {
        KafkaUtil.topic = System.getProperty("kafka.topic", "test_data_center");

        Properties properties = initConsumer(new Properties());
        producer = new KafkaProducer<>(properties);
    }

    private static Properties initConsumer(Properties properties) {
        String kafkaServerUrl = System.getProperty("kafka.server", "192.168.2.55:9092");
        logger.info("producer server path use [{}]", kafkaServerUrl);

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerUrl);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getTypeName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getTypeName());
        return properties;
    }

    private static Properties initProducer(Properties properties) {
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getTypeName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getTypeName());
        return properties;
    }

    public static Future<RecordMetadata> send(String message) {
        return producer.send(new ProducerRecord<>(topic, message));
    }
}
