package learn.demo.webflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Zephyr
 * @date 2020/11/19.
 */
@RestController
public class DemoController {

    @GetMapping("/text")
    public Mono<String> text(String param) {
        return Mono.justOrEmpty("param = " + param);
    }


}
