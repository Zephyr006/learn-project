package learn.demo.webclient.impl;

import learn.demo.webflux.entity.Blog;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

/**
 * Blog相关请求api的声明
 *
 * @author Zephyr
 * @date 2020/11/22.
 */
@ApiServer(url = "http://127.0.0.1:8080/mongo/blogs")
public interface IBlogApi {

    @GetMapping("/blogs")
    Flux<Blog> getAllBlog();
}
