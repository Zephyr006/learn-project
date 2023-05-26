package learn.light4j;

import com.networknt.server.Server;

/**
 * @author Zephyr
 * @since 2020-11-30.
 */
public class Light4jServer {

    // 也可以在命令行中运行：mvn clean package exec:exec
    public static void main(String[] args) {
        long nowTime = System.currentTimeMillis();

        Server.main(args);
        System.out.println(String.format("%nLight-4j Server started in  %d ms.%n", System.currentTimeMillis() - nowTime));
    }

}
