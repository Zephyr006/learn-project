package learn.light4j.custom;

import com.networknt.body.BodyHandler;
import com.networknt.config.Config;
import com.networknt.exception.ApiException;
import com.networknt.exception.ClientException;
import com.networknt.exception.ExceptionConfig;
import com.networknt.exception.ExceptionHandler;
import com.networknt.exception.FrameworkException;
import com.networknt.handler.Handler;
import com.networknt.handler.MiddlewareHandler;
import com.networknt.utility.ModuleRegistry;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Collections;
import java.util.Map;

/**
 * 自定义异常拦截
 * @see com.networknt.exception.ExceptionHandler
 *
 * @author Zephyr
 * @date 2020/12/3.
 */
public class CustomExceptionHandler implements MiddlewareHandler {
    static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    public static final String CONFIG_NAME = "exception";
    static final ExceptionConfig config =
            (ExceptionConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, ExceptionConfig.class);

    static final String STATUS_RUNTIME_EXCEPTION = "ERR10010";
    static final String STATUS_UNCAUGHT_EXCEPTION = "ERR10011";

    private volatile HttpHandler next;

    public CustomExceptionHandler() {

    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        // dispatch here to make sure that all exceptions will be capture in this handler
        // otherwise, some of the exceptions will be captured in Connectors class in Undertow
        // As we've updated Server.java to redirect the logs to slf4j but still it make sense
        // to handle the exception on our ExcpetionHandler.
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        try {
            Handler.next(exchange, next);
        } catch (Throwable e) {
            // 自定义 error 日志输出格式
            HttpString requestMethod = exchange.getRequestMethod();
            Map params = Collections.emptyMap();
            if (Methods.GET.equals(requestMethod)) {
                params =  exchange.getQueryParameters();
            }
            else if (Methods.POST.equals(requestMethod)) {
                params = (Map<String, Object>) exchange.getAttachment(BodyHandler.REQUEST_BODY);
            }
            logger.error("Unexpected exception on {}, params = {}", exchange, params, e);

            if(exchange.isResponseChannelAvailable()) {
                //handle exceptions
                if(e instanceof RuntimeException) {
                    // check if it is FrameworkException which is subclass of RuntimeException.
                    if(e instanceof FrameworkException) {
                        FrameworkException fe = (FrameworkException)e;
                        exchange.setStatusCode(fe.getStatus().getStatusCode());
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send(fe.getStatus().toString());
                        logger.error(fe.getStatus().toString(), e);
                    } else {
                        setExchangeStatus(exchange, STATUS_RUNTIME_EXCEPTION);
                    }
                } else {
                    if(e instanceof ApiException) {
                        ApiException ae = (ApiException)e;
                        exchange.setStatusCode(ae.getStatus().getStatusCode());
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send(ae.getStatus().toString());
                        logger.error(ae.getStatus().toString(), e);
                    } else if(e instanceof ClientException){
                        ClientException ce = (ClientException)e;
                        if(ce.getStatus().getStatusCode() == 0){
                            setExchangeStatus(exchange, STATUS_UNCAUGHT_EXCEPTION);
                        } else {
                            exchange.setStatusCode(ce.getStatus().getStatusCode());
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                            exchange.getResponseSender().send(ce.getStatus().toString());
                        }

                    } else {
                        setExchangeStatus(exchange, STATUS_UNCAUGHT_EXCEPTION);
                    }
                }
            }
        } finally {
            // at last, clean the MDC. Most likely, correlationId in side.
            //logger.debug("Clear MDC");
            MDC.clear();
        }
    }

    @Override
    public HttpHandler getNext() {
        return next;
    }

    @Override
    public MiddlewareHandler setNext(final HttpHandler next) {
        Handlers.handlerNotNull(next);
        this.next = next;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return config.isEnabled();
    }

    @Override
    public void register() {
        ModuleRegistry.registerModule(ExceptionHandler.class.getName(), Config.getInstance().getJsonMapConfigNoCache(CONFIG_NAME), null);
    }

}
