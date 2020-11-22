package learn.demo.webflux.routerfunction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * RouterFunction编程的路由注册 （注册 url path）
 * @author Zephyr
 * @date 2020/11/22.
 */
@Configuration
public class RouterRegister {

    @Bean
    public RouterFunction<ServerResponse> blogRouter(DemoRouterFunctionHandler handler) {
                // RequestPredicates.path == @RequestMapping
                // RequestPredicates.GET("/") == RequestMethod.GET
        return RouterFunctions.nest(
                RequestPredicates.path("/router/blog"),
                RouterFunctions
                        .route(RequestPredicates.GET("/"), handler::getAllBlog)

                        .andRoute(RequestPredicates.POST("/")
                                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON_UTF8)), handler::createBlog)

                        .andRoute(RequestPredicates.DELETE("/{id}"), handler::deleteBlogById)
        );
    }

}
