package learn.demo.webclient.api;

import learn.demo.webclient.interfaces.ApiServer;
import learn.demo.webflux.entity.Blog;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Flux;

/**
 * Blog相关请求api的声明
 *
 * @author Zephyr
 * @date 2020/11/22.
 */
@ApiServer(baseUrl = "http://127.0.0.1:8080/mongo")
public interface IBlogApi {

    @GetMapping("/blogs")
    Flux<Blog> getAllBlog();

}
