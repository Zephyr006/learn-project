package learn.demo.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.HashMap;

/**
 * @author Zephyr
 * @date 2020/11/15.
 */
public class VertxWebVerticle extends AbstractVerticle {

    @Override
    public void start() {
        // This handler will be called for every request
        HttpServer httpServer = vertx.createHttpServer();
        Router router = Router.router(vertx);

        router.route().handler(routingContext -> {

            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");

            response.end("Hello World from Vert.x-Web!");
        });


        Route route2 = router.route("/json").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            // 由于我们会在不同的处理器里写入响应，因此需要启用分块传输
            // 仅当需要通过多个处理器输出响应时才需要
            response.setChunked(true);

            HashMap<Object, Object> map = new HashMap<>();
            map.put("hashkey", "hashvalue");
            response.write(String.valueOf(map));

            routingContext.next();
        });

        Route route3 = router.route("/text").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();

            response.setChunked(true);
            response.write("routingContext plain text");

            routingContext.vertx().setTimer(3000, tid -> routingContext.next());
        });

        Route route4 = router.route("/others").handler(routingContext -> {

            HttpServerResponse response = routingContext.response();
            response.write("route3");

            // 结束响应
            routingContext.response().end();
        });


        httpServer.requestHandler(router).listen(8080);
    }
}
