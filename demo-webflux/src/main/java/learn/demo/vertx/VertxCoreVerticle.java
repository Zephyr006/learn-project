package learn.demo.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;

/**
 * @author Zephyr
 * @since 2020-11-15.
 */
public class VertxCoreVerticle extends AbstractVerticle {

    @Override
    public void start() {

        // This handler gets called for each request that arrives on the server
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {
            // This handler gets called for each request that arrives on the server
            HttpServerResponse response = request.response();
            response.putHeader("content-type", "text/plain");

            // Write to the response and end it
            response.end("Hello World!");
        });

        server.listen(8080);
    }
}
