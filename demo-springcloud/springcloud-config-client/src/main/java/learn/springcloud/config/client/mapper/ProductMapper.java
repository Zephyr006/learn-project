package learn.springcloud.config.client.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.springcloud.config.client.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductMapper extends BaseMapper<Product> {


    default int updateQuantity(Long pid, Integer quantity) {
        LambdaUpdateWrapper<Product> wrapper = Wrappers.<Product>lambdaUpdate()
                .eq(Product::getId, pid)
                .setSql("quantity = quantity - " + quantity);
        return update(null, wrapper);
    }
}
