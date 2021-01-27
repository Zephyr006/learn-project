package learn.datasource.mapper.gk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.datasource.datasource.GkDataSource;
import learn.datasource.entity.gk.GkAccount;
import learn.datasource.registrar.DataServerMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Zephyr
 * @date 2021/1/21.
 */
@DataServerMapper(dataSource = GkDataSource.class)
@Repository
public interface GkAccountMapper extends BaseMapper<GkAccount> {


}
