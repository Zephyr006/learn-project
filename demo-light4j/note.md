## 添加新的 Handler（等价于Controller）
1. 创建的 Handler 实现 LightHttpHandler 接口
2. 在 handler.yml 配置文件中注册 handler
3. 在 handler.yml 配置文件中声明 path
4. 在 openapi.yaml 配置文件中声明 path(if enableVerifyJwt is enable，配置 swagger 校验)


## 读取请求中的参数

```java
class Param {
    // 获取 get 请求的参数值
    Map<String, Deque<String>> params = exchange.getQueryParameters();
    String idStr = params.get("id").getFirst();
    
    // 获取 post 请求中，RequestBody 包含的参数值
    Map<String, Object> requestBody = (Map<String, Object>) exchange.getAttachment(BodyHandler.REQUEST_BODY);
    Long id = pauseLong(requestBody.get("id"));
    
    // 获取 url 路径上的参数值
    Map<String, Deque<String>> pathParameters = exchange.getPathParameters();
}
```

