package learn.light4j.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.body.BodyHandler;
import com.networknt.handler.LightHttpHandler;
import learn.light4j.constants.ErrorEnum;
import learn.light4j.constants.HttpStrings;
import learn.light4j.constants.JwtConstants;
import learn.light4j.constants.MediaType;
import learn.light4j.util.JwtUtils;
import learn.light4j.util.Results;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 根据用户 id 和 dataCenterId 生成 token
 *
 * @author Zephyr
 * @date 2020/12/1.
 */
public class JwtTokenGenerateHandler implements LightHttpHandler {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenGenerateHandler.class);
    private static final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // 返回响应 200
        exchange.getResponseHeaders().add(HttpStrings.Content_Type, MediaType.APPLICATION_JSON_UTF8_VALUE);
        exchange.setStatusCode(200);

        try {
            Map<String, Object> requestBody = (Map<String, Object>) exchange.getAttachment(BodyHandler.REQUEST_BODY);
            Long appId = this.pauseLong(requestBody.get(JwtConstants.APP_ID_KEY));
            Long dataCenterId = this.pauseLong(requestBody.get(JwtConstants.DATA_CENTER_ID_KEY));
            String serviceName = (String) requestBody.get(JwtConstants.SERVICE_NAME_KEY);

            if (appId == null || dataCenterId == null) {
                exchange.getResponseSender().send(mapper.writeValueAsString(Results.fail(ErrorEnum.LACK_PARAM)));
            } else {
                String token = JwtUtils.encode(appId, dataCenterId, serviceName, JwtConstants.JWT_KEY, false);
                exchange.getResponseSender().send(mapper.writeValueAsString(Results.success(token)));
            }
        } catch (Exception e) {
            log.error("Jwt token 生成失败！", e);
            exchange.setStatusCode(500);
            exchange.getResponseSender().send(mapper.writeValueAsString(Results.fail(ErrorEnum.INNER_ERROR)));
        }
    }


    /**
     * 解析参数值为 Long 类型
     */
    private Long pauseLong(Object value) {
        if (value instanceof Integer) {
            return ((Integer)value).longValue();
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        return null;
    }

}
