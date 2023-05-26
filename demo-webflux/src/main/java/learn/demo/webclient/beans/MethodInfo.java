package learn.demo.webclient.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * WebClient要调用的方法信息
 *
 * @author Zephyr
 * @since 2020-11-23.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodInfo {

    /**
     * 方法上的url
     */
    private String url;

    /**
     * 请求的方法
     */
    private HttpMethod httpMethod;

    /**
     * 请求参数
     */
    private Map<String, Object> params;

    /**
     * ‘@RequestBody’注解对应信息
     */
    private Mono requestBody;

    /**
     * 请求body的类型
     */
    private Class<?> bodyElementType;

    /**
     * 返回是Mono还是Flux
     */
    private boolean isReturnFlux;

    /**
     * 返回值的类型
     */
    private Class<?> returnElementType;

}
