package learn.springcloud.config.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.springcloud.config.client.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OrderMapper extends BaseMapper<Order> {
}
