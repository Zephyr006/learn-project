package learn.demo.vertx;

import io.vertx.core.AbstractVerticle;

/**
 * @author Zephyr
 * @date 2020/11/15.
 */
public class MainVerticle extends AbstractVerticle {

    public void start() {
        vertx.deployVerticle(MyFirstVerticle.class.getName());
    }
}
