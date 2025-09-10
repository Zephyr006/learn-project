package learn.springcloud.config.client.controller;

import learn.springcloud.config.client.api.ProductApi;
import learn.springcloud.config.client.entity.Order;
import learn.springcloud.config.client.entity.Product;
import learn.springcloud.config.client.mapper.OrderMapper;
import learn.springcloud.config.client.mapper.ProductMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class ProductController implements ProductApi {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private OrderMapper orderMapper;

    @Override
    public Product getProduct(Long pid) {
        Product product = productMapper.selectById(pid);
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long orderProduct( Long pid,  Long userId,  Integer amount) {
        // todo 这里应该对库存加锁
        Product product = productMapper.selectById(pid);
        if (product == null) {
            throw new IllegalArgumentException("要下单的商品不存在");
        }
        if (product.getQuantity() < amount) {
            throw new IllegalArgumentException("下单的商品库存不足");
        }
        // 组装数据
        Order order = new Order();
        order.setUserId(userId);
        order.setPid(pid);
        order.setAmount(amount);

        // 执行数据更新
        productMapper.updateQuantity(pid, amount);
        orderMapper.insert(order);
        return order.getId();
    }

    private Long getUserIdFromToken(String token) {
        return 0L;
    }
}
