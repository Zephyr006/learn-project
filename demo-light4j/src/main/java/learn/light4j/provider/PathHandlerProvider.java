
package learn.light4j.provider;


import com.networknt.config.Config;
import com.networknt.handler.HandlerProvider;
import com.networknt.utility.StringUtils;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.builder.PredicatedHandlersParser;
import io.undertow.server.handlers.resource.FileResourceManager;
import learn.light4j.config.WebServerConfig;
import learn.light4j.handler.CancelSubscribeHandler;
import learn.light4j.handler.SubscribeMessageHandler;

import java.io.File;

import static io.undertow.Handlers.resource;

/**
 * @author cao
 * @time: 2020/12/2 6:18 下午
 */
public class PathHandlerProvider implements HandlerProvider {

    public static final String CONFIG_NAME = "webserver";

    private static WebServerConfig config;


    public PathHandlerProvider() {
        loadConfig();
    }

    private void loadConfig() {
        config = (WebServerConfig)Config.getInstance().getJsonObjectConfig(CONFIG_NAME, WebServerConfig.class);

        String baseDir = System.getProperty("base");
        if (StringUtils.isNotBlank(baseDir)) {
            config.setBase(baseDir);
        }
    }

    @Override
    public HttpHandler getHandler() {
        return Handlers.predicates(
                PredicatedHandlersParser.parse(config.getRewrite(), PathHandlerProvider.class.getClassLoader()),
                new PathHandler(resource(new FileResourceManager(
                        new File(config.getBase()), config.getTransferMinSize())))
                        .addPrefixPath("/api/subscribe", new SubscribeMessageHandler())
                        .addPrefixPath("/api/cancelSubscribe", new CancelSubscribeHandler())
                        //.addPrefixPath("/api/login", new LoginHandler())
                        //.addPrefixPath("/api/accessTokenRefresh", new RefreshTokenHandler())
                        //.addPrefixPath("/api/saveOperation", new SaveOperationHandler())
                        //.addPrefixPath("/api/queryOperation", new QueryOperationHandler())
                        //.addPrefixPath("/api/deleteOperation", new DeleteOperationHandler())
                        //.addPrefixPath("/api/buriedPoint", new StatisticHandler())
        );
    }
}
