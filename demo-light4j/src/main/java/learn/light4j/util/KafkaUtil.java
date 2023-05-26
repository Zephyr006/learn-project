package learn.light4j.util;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.SerializationException;
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
 * @since 2020-11-19
 */
public class KafkaUtil {
    public static final Logger logger = LoggerFactory.getLogger(KafkaUtil.class);
    public static final Callback DEFAULT_CALLBACK;
    private static KafkaProducer<String, String> producer;
    private static KafkaConsumer<String, String> consumer;
    private static String topic;

    static {
        KafkaUtil.topic = System.getProperty("kafka.topic", "test_data_center");
        String kafkaServerUrl = System.getProperty("kafka.server", "192.168.2.55:9092");
        logger.info("kafka server path use [{}]", kafkaServerUrl);

        initProducer(kafkaServerUrl);
        initConsumer(kafkaServerUrl, "test");

        DEFAULT_CALLBACK = (metadata, exception) -> {
            if (exception != null) {
                if (exception instanceof AuthenticationException) {
                    logger.error("Kafka发送消息时身份验证失败！", exception);
                } else if (exception instanceof AuthorizationException) {
                    logger.error("Kafka生产者没有写权限！", exception);
                } else if (exception instanceof IllegalStateException) {
                    logger.error("Kafka生产者指定了transactional.id但事务没有启动，或者生产者关闭后发送了生产请求", exception);
                } else if (exception instanceof InterruptException) {
                    logger.error("Kafka生产者线程阻塞等待过程中被打断", exception);
                } else if (exception instanceof SerializationException) {
                    logger.error("Kafka生产者配置的序列化器无法序列化对应的 key 或 value", exception);
                } else {
                    logger.error("Kafka生产者发送消息时出现了未知错误", exception);
                }
            } else {
                logger.debug("kafka 消息发送成功，metadata=【{}】", metadata.toString());
            }
        };
    }


    private static void initProducer(String bootstrapServersConfig) {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "1");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getTypeName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getTypeName());

        producer = new KafkaProducer<>(properties);
    }

    private static void initConsumer(String bootstrapServersConfig, String consumerGroupId) {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServersConfig);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getTypeName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getTypeName());

        consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(KafkaUtil.topic));
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
