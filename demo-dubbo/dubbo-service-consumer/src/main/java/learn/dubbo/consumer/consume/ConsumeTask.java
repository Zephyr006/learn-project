package learn.dubbo.consumer.consume;

import learn.dubbo.service.api.GreetingService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * CommandLineRunner: 标识一个 bean 包含在 SpringApplication 中时应该运行的接口。
 *
 * @author Zephyr
 * @date 2023-09-07
 */
@Component
public class ConsumeTask implements CommandLineRunner {

    // 通过@DubboReference 从 Dubbo 获取了一个 RPC 订阅
    @DubboReference
    GreetingService greetingService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(args);
        String result = greetingService.sayHi("from consumer");
        System.out.println("Receive result ======> " + result);

        new Thread(()-> {
            int times = 10;
            while (times-- > 0) {
                try {
                    Thread.sleep(5000);
                    System.out.println(new Date() + "  Receive result ======> " + greetingService.sayHi("world"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
