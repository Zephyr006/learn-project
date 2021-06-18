package learn.base.test.connect;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import learn.base.BaseTest;
import learn.base.utils.FileLoader;
import org.junit.Assert;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author Zephyr
 * @date 2021/3/7.
 */
public class RedisConnectTest extends BaseTest {
    private static final String FILE_PATH = "conn-test.properties";


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new RedisConnectTest().testConnect();
    }

    public void testConnect() throws ExecutionException, InterruptedException {
        if (!checkContext()) {
            return;
        }
        final Properties props = FileLoader.loadProperties(FILE_PATH);

        // <1> 创建单机连接的连接信息
        RedisURI redisUri = RedisURI.builder()
                .withHost(props.get("host").toString())
                .withPort(6379)
                .withTimeout(Duration.of(5, ChronoUnit.SECONDS))
                .build();

        // <2> 创建客户端
        RedisClient redisClient = RedisClient.create(redisUri);
        // <3> 创建线程安全的连接
        StatefulRedisConnection<String, String> connection = redisClient.connect();

        // <4> 创建同步命令
        RedisAsyncCommands<String, String> asyncCommand = connection.async();
        RedisCommands<String, String> syncCommands = connection.sync();
        SetArgs setArgs = SetArgs.Builder.nx().ex(5);

        final String redisValue = "redis_connect_test_value";
        String result = syncCommands.set("redis_connect_test_key", redisValue, setArgs);
        Assert.assertTrue("OK".equalsIgnoreCase(result));
        String resultValue = syncCommands.get("redis_connect_test_key");
        Assert.assertTrue(redisValue.equalsIgnoreCase(resultValue));

        RedisFuture<String> future = asyncCommand.set("redis_connect_async_test_key", redisValue, setArgs);
        String asyncResult = future.get();
        Assert.assertTrue("OK".equalsIgnoreCase(asyncResult));
        final String asyncConnValue = asyncCommand.get("redis_connect_async_test_key").get();


        // <5> 关闭连接
        connection.close();
        // <6> 关闭客户端
        redisClient.shutdown();

        System.out.println("--- Redis connect successfully. ---");
        //链接：https://juejin.cn/post/6844903954778701832
    }
}
