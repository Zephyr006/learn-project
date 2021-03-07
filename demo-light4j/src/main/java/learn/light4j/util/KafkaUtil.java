package learn.light4j.util;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author Zephyr
 * @date 2020/11/19
 */
public class KafkaUtil {
    public static final Logger logger = LoggerFactory.getLogger(KafkaUtil.class);
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;
    private static String topic;

    static {
        KafkaUtil.topic = System.getProperty("kafka.topic", "test_data_center");
        String kafkaServerUrl = System.getProperty("kafka.server", "192.168.2.55:9092");
        logger.info("kafka server path use [{}]", kafkaServerUrl);

        initProducer(kafkaServerUrl);
        initConsumer(kafkaServerUrl);
    }


    private static Properties initProducer(String bootstrapServersConfig) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getTypeName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getTypeName());

        producer = new KafkaProducer<>(properties);
        return properties;
    }

    private static Properties initConsumer(String bootstrapServersConfig) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test");
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getTypeName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getTypeName());

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(KafkaUtil.topic));
        return properties;
    }

    public static Future<RecordMetadata> send(String message) {
        return producer.send(new ProducerRecord<>(topic, message));
    }

    public static Future<RecordMetadata> send(String topic, String message) {
        return producer.send(new ProducerRecord<>(topic, message));
    }

    public static Future<RecordMetadata> send(String topic, String message, Callback callback) {
        return producer.send(new ProducerRecord<>(topic, message), callback);
    }


    public static ConsumerRecords<String, String> poll(Duration duration) {
        return consumer.poll(duration != null ? duration : Duration.ofSeconds(5));
    }

}