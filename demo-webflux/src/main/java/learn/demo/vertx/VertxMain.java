package learn.demo.vertx;

import io.vertx.core.Vertx;

/**
 * @author Zephyr
 * @since 2020-11-15.
 */
public class VertxMain {
    public static void main(String[] args){
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(VertxCoreVerticle.class.getName());
    }
}
