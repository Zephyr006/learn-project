package learn.demo.webclient.interfaces.impl;


import learn.demo.webclient.beans.MethodInfo;
import learn.demo.webclient.beans.ServerInfo;
import learn.demo.webclient.interfaces.ApiServer;
import learn.demo.webclient.interfaces.ProxyCreator;
import learn.demo.webclient.interfaces.RestHandler;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于JdkProxy的代理对象生成
 *
 * @author Zephyr
 * @date 2020/11/22.
 */

public class JdkProxyCreator implements ProxyCreator {

    @Override
    public Object create(Class<?> type) {
        // 服务器相关信息
        ServerInfo serverInfo = this.extractServerInfo(type);
        System.out.println(serverInfo);

        RestHandler restHandler = new WebClientRestHandler(serverInfo);


        InvocationHandler invocationHandler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                // 根据方法和参数得到调用方法的信息
                MethodInfo methodInfo = extractMethodInfo(method, args);
                return restHandler.invokeRest(methodInfo);
            }
        };

        Class[] classes = {type};
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), classes, invocationHandler);
    }


    /**
     * 根据接口得到远程api服务器信息
     */
    private ServerInfo extractServerInfo(Class<?> type) {
        ServerInfo serverInfo = new ServerInfo();
        ApiServer apiServer = type.getAnnotation(ApiServer.class);
        serverInfo.setUrl(apiServer.baseUrl());
        return serverInfo;
    }


    /**
     * 根据方法定义和调用参数得到要调用方法的信息
     */
    private MethodInfo extractMethodInfo(Method method, Object[] args) {
        MethodInfo methodInfo = new MethodInfo();

        // 封装url和请求方式
        extractRequestUrlAndMethod(method, methodInfo);

        // 封装请求参数和body
        extractPathVariableAndRequestBody(method, args, methodInfo);

        // 封装返回信息
        extractReturnInfo(method, methodInfo);

        return methodInfo;
    }


    /**
     * 抽取返回的是Mono还是Flux和返回对象的类型
     */
    private void extractReturnInfo(Method method, MethodInfo methodInfo) {

        // 返回Mono还是Flux
        // isAssignableFrom() 判断类型是否是某个类的子类  vs instanceof  判断实例是否是某个的子类
        boolean isFlux = method.getReturnType().isAssignableFrom(Flux.class);
        methodInfo.setReturnFlux(isFlux);

        // 得到返回对象的实际类型
        Class<?> elementType = extractElementType(method.getGenericReturnType());
        methodInfo.setReturnElementType(elementType);

    }

    /**
     * 抽取请求参数和请求body
     */
    private void extractPathVariableAndRequestBody(Method method, Object[] args, MethodInfo methodInfo) {
        Map<String, Object> params = new LinkedHashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            // 参数信息
            PathVariable annotation = parameters[i].getAnnotation(PathVariable.class);
            if (annotation != null) {
                params.put(annotation.value(), args[i]);
                methodInfo.setParams(params);
            }

            // 请求body和body类型
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                methodInfo.setRequestBody((Mono<?>) args[i]);
                methodInfo.setBodyElementType(extractElementType(parameters[i].getParameterizedType()));
            }
        }
    }

    /**
     * 抽取请求url 和请求方式
     */
    private void extractRequestUrlAndMethod(Method method, MethodInfo methodInfo) {
        Annotation[] annotations = method.getAnnotations();
        Arrays.stream(annotations).forEach(annotation -> {
            if (annotation instanceof GetMapping) {
                GetMapping getMethod = (GetMapping) annotation;
                methodInfo.setUrl(getMethod.value()[0]);
                methodInfo.setHttpMethod(HttpMethod.GET);
            } else if (annotation instanceof PostMapping) {
                PostMapping postMethod = (PostMapping) annotation;
                methodInfo.setUrl(postMethod.value()[0]);
                methodInfo.setHttpMethod(HttpMethod.POST);
            } else if (annotation instanceof DeleteMapping) {
                DeleteMapping deleteMethod = (DeleteMapping) annotation;
                methodInfo.setUrl(deleteMethod.value()[0]);
                methodInfo.setHttpMethod(HttpMethod.DELETE);
            } else if (annotation instanceof PutMapping) {
                PutMapping putMethod = (PutMapping) annotation;
                methodInfo.setUrl(putMethod.value()[0]);
                methodInfo.setHttpMethod(HttpMethod.PUT);
            }
        });
    }


    /**
     *得到泛型的实际类型
     */
    private Class<?> extractElementType(Type genericReturnType) {
        Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }


}
