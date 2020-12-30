package learn.light4j.provider;

import com.alibaba.fastjson.JSON;
import com.networknt.config.Config;
import com.networknt.server.StartupHookProvider;
import com.networknt.utility.StringUtils;
import learn.light4j.config.KafkaConfig;
import learn.light4j.domain.EventTrackingMessage;
import learn.light4j.domain.StatisticService;
import learn.light4j.domain.subscribe.SubscribeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * @author: cao
 * @time: 2020/12/3 5:44 下午
 */
@Slf4j
public class KafkaStartupHookProvider implements StartupHookProvider {

    public static KafkaProducer<String, byte[]> producer;
    public static KafkaConsumer<String, byte[]> SUBSCRIBE_CONSUMER;
    public static KafkaConsumer<String, byte[]> STATISTIC_CONSUMER;
    public static KafkaConfig KAFKA_CONFIG;



    @Override
    public void onStartup() {
        initConfig();
        initProducer();
        initConsumer();
        beginConsume();
    }

    private void beginConsume() {
        ExecutorService threadPool = Executors.newFixedThreadPool(3, new ThreadFactory() {
            private final AtomicInteger nextId = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                String name = "Worker-" + nextId.getAndIncrement();
                return new Thread(null, r, name, 0);
            }
        });

        SubscribeService subscribePool = SubscribeService.getInstance();
        StatisticService statisticExecutor = StatisticService.getInstance();
        threadPool.execute(() -> beginConsumeKafka(subscribePool::receiveMessage, SUBSCRIBE_CONSUMER));
        threadPool.execute(() -> beginConsumeKafka(statisticExecutor::receiverMessage, STATISTIC_CONSUMER));
    }

    private void beginConsumeKafka(
            Consumer<List<EventTrackingMessage>> messageConsumer,
            KafkaConsumer<String, byte[]> kafkaConsumer) {
        while(true){
            ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(Duration.ofSeconds(Integer.MAX_VALUE));
            if (records.count() > 0) {
                List<EventTrackingMessage> messages = new ArrayList<>();
                for (ConsumerRecord<String, byte[]> record : records) {
                    EventTrackingMessage message = JSON.parseObject(record.value(), EventTrackingMessage.class);
                    messages.add(message);
                }
                messageConsumer.accept(messages);
            }
        }
    }

    private void initConsumer() {
        Properties properties = new Properties();
        properties.put("group.id", KAFKA_CONFIG.getSubscribeGroupId());
        properties.put("bootstrap.servers", KAFKA_CONFIG.getBootstrapServers());
        properties.put("key.deserializer", KAFKA_CONFIG.getKeyDeserializer());
        properties.put("value.deserializer", KAFKA_CONFIG.getValueDeserializer());
        SUBSCRIBE_CONSUMER = new KafkaConsumer<>(properties);
        SUBSCRIBE_CONSUMER.subscribe(Collections.singletonList(KAFKA_CONFIG.getTopic()));


        properties.put("group.id", KAFKA_CONFIG.getStatisticGroupId());
        STATISTIC_CONSUMER = new KafkaConsumer<>(properties);
        STATISTIC_CONSUMER.subscribe(Collections.singletonList(KAFKA_CONFIG.getTopic()));
    }

    private void initProducer() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_CONFIG.getBootstrapServers());
        properties.put("key.serializer", KAFKA_CONFIG.getKeySerializer());
        properties.put("value.serializer", KAFKA_CONFIG.getValueSerializer());
        producer = new KafkaProducer<>(properties);
    }


    private void initConfig() {

        String envConfig = System.getProperty("env", "dev");
        log.debug("kafka 加载 {} 环境配置", envConfig);
        String config = String.format("%s-%s", "kafka", envConfig);
        KAFKA_CONFIG = (KafkaConfig) Config.getInstance().getJsonObjectConfig(config, KafkaConfig.class);

        String bootstrapServers = System.getProperty("kafka.bootstrap-servers");
        if (StringUtils.isNotBlank(bootstrapServers)) {
            KAFKA_CONFIG.setBootstrapServers(bootstrapServers);
        }

        String topic = System.getProperty("kafka.topic");
        if (StringUtils.isNotBlank(topic)) {
            KAFKA_CONFIG.setTopic(topic);
        }
    }
}
