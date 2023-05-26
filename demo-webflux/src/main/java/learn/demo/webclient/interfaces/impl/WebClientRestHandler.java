package learn.demo.webclient.interfaces.impl;

import learn.demo.webclient.beans.MethodInfo;
import learn.demo.webclient.beans.ServerInfo;
import learn.demo.webclient.interfaces.RestHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 基于WebClient的http请求处理实现类
 * @author Zephyr
 * @since 2020-11-28.
 */
public class WebClientRestHandler implements RestHandler {

    private WebClient webClient;

    public WebClientRestHandler(ServerInfo serverInfo) {
        this.webClient = WebClient.create(serverInfo.getUrl());
    }


    @Override
    public Object invokeRest(MethodInfo methodInfo) {

        WebClient.RequestBodyUriSpec requestBodyUriSpec = this.webClient.method(methodInfo.getHttpMethod());

        if (methodInfo.getParams() != null) {
            requestBodyUriSpec.uri(methodInfo.getUrl(), methodInfo.getParams());
        } else {
            requestBodyUriSpec.uri(methodInfo.getUrl());
        }

        if (methodInfo.getRequestBody() != null) {
            requestBodyUriSpec.body(methodInfo.getRequestBody(), methodInfo.getBodyElementType());
        }

        // 发送http请求
        WebClient.ResponseSpec response = requestBodyUriSpec
                .accept(MediaType.APPLICATION_JSON)
                // send request
                .retrieve();

        // 异常处理
        response.onStatus(HttpStatus::is4xxClientError, clientResponse ->
                Mono.error(WebClientResponseException.create(clientResponse.rawStatusCode(),
                        "服务调用异常", null, null, StandardCharsets.UTF_8)));
        response.onStatus(HttpStatus::is5xxServerError, clientResponse ->
                Mono.error(WebClientResponseException.create(clientResponse.rawStatusCode(),
                        "服务器内部异常", null, null, StandardCharsets.UTF_8)));

        // 根据返回值类型返回结果值
        if (methodInfo.isReturnFlux()) {
            return response.bodyToFlux(methodInfo.getReturnElementType());
        } else {
            return response.bodyToMono(methodInfo.getReturnElementType());
        }
    }
}
