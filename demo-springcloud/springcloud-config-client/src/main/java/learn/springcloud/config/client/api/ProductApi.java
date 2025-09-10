package learn.springcloud.config.client.api;

import learn.springcloud.config.client.entity.Product;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface ProductApi {
    @GetMapping("/product/pid")
    Product getProduct(@RequestParam Long pid);

    @PostMapping("/product/order")
    Long orderProduct(@RequestParam Long pid, @RequestParam Long userId, @RequestParam Integer amount);
}
