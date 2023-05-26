package learn.base.test.connect;

import learn.base.BaseTest;
import learn.base.utils.FileLoader;
import learn.base.utils.KafkaUtil;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.junit.Assert;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Zephyr
 * @since 2021-3/7.
 */
public class KafkaConnectTest extends BaseTest {
    private static final String FILE_PATH = "conn-test.properties";
    private static final String BOOTSTRAP_SERVERS_CONFIG_KEY = "kafka.server";
    private static final String KAFKA_TOPIC_KEY = "kafka.topic";


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new KafkaConnectTest().testConnect();
    }

    public void testConnect() throws ExecutionException, InterruptedException {
        if (!checkContext()) {
            return;
        }
        final String topic = "kafka_connect_test";


        final Properties props = FileLoader.loadProperties(FILE_PATH);
        String host = String.valueOf(props.get("host"));
        System.setProperty(BOOTSTRAP_SERVERS_CONFIG_KEY, "localhost:9092");
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

    // @Test
    public void testKafkaAdminClient() throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "39.:10003");
        props.setProperty(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "3111");

        final AdminClient adminClient = AdminClient.create(props);
        DescribeClusterResult clusterResult = adminClient.describeCluster();
        KafkaFuture<Collection<Node>> nodes = clusterResult.nodes();
        nodes.get().forEach(System.out::println);

        String consumerGroupId = "submit-log-group";
        DescribeConsumerGroupsResult consumerGroupsResult = adminClient.describeConsumerGroups(Arrays.asList(consumerGroupId));
        KafkaFuture<Map<String, ConsumerGroupDescription>> allConsumerGroup = consumerGroupsResult.all();
        ConsumerGroupDescription consumerGroupDescription = allConsumerGroup.get().get(consumerGroupId);
        System.out.println(consumerGroupDescription);


    }
}
