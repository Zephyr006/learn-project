package learn.demo.webflux.routerfunction;

import learn.demo.webflux.entity.Blog;
import learn.demo.webflux.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * 接收请求数据使用 ServerRequest ，返回响应数据使用 ServerResponse
 *
 * @author Zephyr
 * @since 2020-11-22.
 */
@Component
public class DemoRouterFunctionHandler {

    @Autowired
    private BlogRepository blogRepository;


    //@RequestMapping(value = "/router/blog/", method = RequestMethod.GET)
    public Mono<ServerResponse> getAllBlog(ServerRequest request) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(blogRepository.findAll(), Blog.class);
    }


    //@RequestMapping(value = "/router/blog/", method = RequestMethod.POST)
    public Mono<ServerResponse> createBlog(ServerRequest request) {
        Mono<Blog> blogMono = request.bodyToMono(Blog.class);

        return blogMono.flatMap(blog -> {
            // 可以在这里校验参数值合法性
            Assert.notNull(blog.getName(), "name can't be null");
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON_UTF8)
                    .body(blogRepository.saveAll(blogMono), Blog.class);
        }).switchIfEmpty(ServerResponse.badRequest().build());
    }


    //@RequestMapping(value = "/router/blog/{id}", method = RequestMethod.DELETE)
    public Mono<ServerResponse> deleteBlogById(ServerRequest request) {
        String id = request.pathVariable("id");
        return blogRepository.findById(id)
                .flatMap(blog -> blogRepository.delete(blog).then(ServerResponse.ok().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


}
