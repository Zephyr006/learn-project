package learn.springcloud.gateway.controller;

import learn.springcloud.gateway.client.ProductClient;
import learn.springcloud.gateway.dto.ProductReq;
import learn.springcloud.gateway.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
public class OrderController {
    @Resource
    UserService userService;
    @Resource
    ProductClient productClient;

    @PostMapping("/product/order")
    @Transactional(rollbackFor = Exception.class)
    public Long orderProduct(@RequestBody ProductReq req) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("X-token");

        // 统一异常处理 - 未登录异常
        Long userId = userService.auth(token);
        // Long orderId = productClient.orderProduct(req.getPid(), userId, req.getAmount());
        return null;
    }
}
