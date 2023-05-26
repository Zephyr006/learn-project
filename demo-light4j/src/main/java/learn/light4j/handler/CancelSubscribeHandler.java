package learn.light4j.handler;

import com.networknt.utility.CollectionUtil;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import learn.light4j.domain.subscribe.SubscribeService;
import learn.light4j.model.BaseResponse;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;

/**
 * @author: cao
 * @time: 2020-12/10 5:51 下午
 */
public class CancelSubscribeHandler implements HttpHandler {

    public static final String CANCEL_SUBSCRIBE_URL = "/api/cancelSubscribe";

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        Integer id = extractId(exchange);
        if (Objects.isNull(id)) {
            exchange.getResponseSender().send("缺少 id");
            return;
        }

        //取消订阅
        SubscribeService.getInstance().removeSubscriber(id);

        exchange.getResponseSender().send(BaseResponse.ok());
    }

    private Integer extractId(HttpServerExchange exchange) {
        Map<String, Deque<String>> queryParameters = exchange.getQueryParameters();
        Deque<String> idDeque = queryParameters.getOrDefault("id", new ArrayDeque<>());
        if (!CollectionUtil.isEmpty(idDeque)) {
            try {
                return Integer.parseInt(idDeque.getFirst());
            }catch (Exception ignored){}
        }
        return null;
    }
}
