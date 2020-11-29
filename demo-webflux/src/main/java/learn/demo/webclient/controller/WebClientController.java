package learn.demo.webclient.controller;

import learn.demo.webclient.api.IBlogApi;
import learn.demo.webflux.entity.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author Zephyr
 * @date 2020/11/22.
 */
@RestController
public class WebClientController {
    @Autowired
    IBlogApi blogApi;

    @GetMapping("webclient/test")
    public Flux<Blog> getAllBlog() {
        return blogApi.getAllBlog();
    }
}
