package learn.tech.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zephyr
 * @date 2020/9/2.
 */
@RestController
@RequestMapping("/springmvc/")
public class SpringMvcController {

    @GetMapping("sleep/{sleepTime}")
    public String mono(@PathVariable Integer sleepTime) {
        long nanoTime = System.nanoTime();
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            System.out.println("exc");
        }
        return "hello webflux, it cost " + (System.nanoTime()-nanoTime) + " ns.";
    }
}
