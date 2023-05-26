package learn.light4j.util;

import com.networknt.body.BodyHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;

import java.io.InputStream;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * @author Zephyr
 * @since 2020-12-28.
 */
public class ServerExchangeUtil {

    /**
     * 获取 get 请求中发送的参数值
     */
    public static Deque<String> getParam(HttpServerExchange exchange, String key) {
        Map<String, Deque<String>> parameters = exchange.getQueryParameters();
        return parameters.get(key);
    }


    public static <T> T postParam(HttpServerExchange exchange, AttachmentKey<T> attachmentKey) {
        return exchange.getAttachment(attachmentKey);
    }

    /**
     * 获取请求路径上携带的参数值，例如：/api/user/{id} 中的 "id"
     */
    public static Deque<String> pathParameters(HttpServerExchange exchange, String key) {
        return exchange.getPathParameters() == null ? null : exchange.getPathParameters().get(key);
    }

    /**
     * 获取请求体中发送的参数值
     * 请求体中的参数解析依赖于 @body 模块
     *
     * @see BodyHandler#handleRequest(io.undertow.server.HttpServerExchange)
     * String unparsedRequestBody = com.networknt.utility.StringUtils.inputStreamToString(exchange.getInputStream(), UTF_8);
     */
    public static Object requestBody(HttpServerExchange exchange) {
        Object requestBody = exchange.getAttachment(BodyHandler.REQUEST_BODY);
        // "application/json" / "multipart/form-data" / "application/x-www-form-urlencoded"
        if (requestBody instanceof Map) {
            return (Map<String, Object>) requestBody;
        }
        // "application/json"
        else if (requestBody instanceof List) {
            return (List<Object>) requestBody;
        }
        //"text/plain"
        else if (requestBody instanceof String) {
            return (String) requestBody;
        }
        // BodyHandler 无法解析的 contentType类型
        else if (requestBody instanceof InputStream) {
            return (InputStream) requestBody;
        }
        return requestBody;
    }


}
