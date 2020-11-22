package learn.demo.webflux.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author Zephyr
 * @date 2020/11/19.
 */
@RestController
public class DemoMonoFluxController {

    @GetMapping("/text")
    public Mono<String> text(String param) {
        return Mono.justOrEmpty("param = " + param);
    }

    @GetMapping("/mono")
    public Mono<String> mono(String param) {
        long timeMillis = System.currentTimeMillis();
        Mono<String> mono = Mono.fromSupplier(
                () -> {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "mono data, param=" + param;
                }
        );
        System.out.println(String.format("mono cost %d ms", System.currentTimeMillis() - timeMillis));
        return mono;
    }

    // fixme 应该逐个返回数据，现在是得到所有数据后一次性返回
    @GetMapping(value = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> flux(String param) {
        if (Integer.parseInt(param) > 3) {
            return Flux.just("Param is too large. I can't wait that long");
        }
        long timeMillis = System.currentTimeMillis();
        Flux<String> flux = Flux.interval(Duration.ofSeconds(1)).fromStream(
                IntStream.range(0, 3).mapToObj(i -> {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return "flux data :" + i;
                })
        );
        System.out.println(String.format("flux cost %d ms", System.currentTimeMillis() - timeMillis));
        return flux;
    }

    /**
     * SSE响应头的格式是固定的
     * SSE本质上也是不断轮询请求接口获取数据。
     * 返回的数据有固定格式：每条数据都以‘data:’开头
     */
    @GetMapping(value = "/sse", produces = "text/event-stream;charset=UTF-8")
    public Mono<ServerSentEvent<Integer>> sse() {
        return Mono.delay(Duration.ofSeconds(1)).fromSupplier(() -> {
            return ServerSentEvent.<Integer>builder().data(new Random().nextInt(100000)).build();
        });
    }

}
