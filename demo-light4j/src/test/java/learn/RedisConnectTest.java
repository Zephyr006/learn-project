package learn;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.Assert;
import org.junit.Test;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author Zephyr
 * @date 2021/3/7.
 */
public class RedisConnectTest {

    @Test
    public void testConnect() {
        // <1> 创建单机连接的连接信息
        RedisURI redisUri = RedisURI.builder()
                .withHost("192.168.2.136")
                .withPort(6379)
                .withTimeout(Duration.of(5, ChronoUnit.SECONDS))
                .build();

        // <2> 创建客户端
        RedisClient redisClient = RedisClient.create(redisUri);
        // <3> 创建线程安全的连接
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        // <4> 创建同步命令
        RedisCommands<String, String> redisCommands = connection.sync();
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);

        String result = redisCommands.set("redis_connect_test_key", "redis_connect_test_value", setArgs);
        Assert.assertTrue("OK".equalsIgnoreCase(result));
        String resultValue = redisCommands.get("redis_connect_test_key");
        Assert.assertTrue("redis_connect_test_value".equalsIgnoreCase(resultValue));

        // <5> 关闭连接
        connection.close();
        // <6> 关闭客户端
        redisClient.shutdown();

        System.out.println("--- Redis connect successfully. ---");
        //链接：https://juejin.cn/post/6844903954778701832
    }
}
