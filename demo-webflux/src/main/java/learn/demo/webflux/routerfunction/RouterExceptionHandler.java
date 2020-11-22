package learn.demo.webflux.routerfunction;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * RouterFunction的全局统一异常处理类
 * 由于包含多个异常处理（WebExceptionHandler）的实现，所以需要把当前异常处理类的优先级调高
 * "@Order"的数值越小，优先级越高
 *
 * @author Zephyr
 * @date 2020/11/22.
 */
@Order(-9)
@Component
public class RouterExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange serverWebExchange, Throwable throwable) {
        ServerHttpResponse response = serverWebExchange.getResponse();
        // 设置异常情况下的响应头和响应体返回类型
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);

        DataBuffer dataBuffer = response.bufferFactory().wrap(throwable.toString().getBytes());
        return response.writeWith(Mono.justOrEmpty(dataBuffer));
    }

}
