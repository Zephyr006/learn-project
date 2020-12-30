package learn.light4j.config;

import lombok.Data;

/**
 * @author: cao
 * @time: 2020/12/3 5:52 下午
 */
@Data
public class KafkaConfig {

    private String bootstrapServers;
    private String topic;
    private String keySerializer;
    private String keyDeserializer;
    private String valueSerializer;
    private String valueDeserializer;
    private String subscribeGroupId;
    private String statisticGroupId;
}
