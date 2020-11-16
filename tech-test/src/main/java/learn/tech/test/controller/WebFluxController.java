package learn.tech.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zephyr
 * @date 2020/9/2.
 */
@RestController
@RequestMapping("/webflux/")
public class WebFluxController {

    //@GetMapping("sleep/{sleepTime}")
    //public Mono<String> mono(@PathVariable Integer sleepTime) {
    //    return Mono.just("request error")
    //            .delayElement(Duration.ofMillis(sleepTime));
    //}
}
