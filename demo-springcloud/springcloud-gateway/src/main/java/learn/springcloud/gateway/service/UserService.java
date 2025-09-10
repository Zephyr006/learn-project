package learn.springcloud.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Value("${sale.user.userId:}")
    Long configedUserId;

    public Long auth(String token) {
    //     此处省略根据 token 解析 userId，可以使用 JWT
        Long userId = parse(token);
        if (configedUserId == null || !configedUserId.equals(userId)) {
        //     登录错误
            throw new IllegalArgumentException("登录错误");
        }
        return userId;
    }

    private Long parse(String token) {
        return 0L;
    }
}
