package learn.demo.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

/**
 * @author Zephyr
 * @date 2020/11/16.
 */
public class VerticleWithVertxWeb2 extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        //Route route = router.route("/url/json").handler(routingContext -> {});

    }
}
