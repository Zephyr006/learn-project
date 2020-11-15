package learn.demo.vertx;

import io.vertx.core.AbstractVerticle;

/**
 * @author Zephyr
 * @date 2020/11/15.
 */
public class MyFirstVerticle extends AbstractVerticle {

    public void start() {
        vertx.createHttpServer().requestHandler(req -> {
            req.response()
                    .putHeader("content-type", "text/plain")
                    .end("Hello World!");
        }).listen(8080);
    }
}
