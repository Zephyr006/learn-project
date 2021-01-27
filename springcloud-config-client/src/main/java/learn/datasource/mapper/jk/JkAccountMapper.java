package learn.datasource.mapper.jk;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.datasource.datasource.JkDataSource;
import learn.datasource.entity.jk.JkAccount;
import learn.datasource.registrar.DataServerMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Zephyr
 * @date 2021/1/21.
 */
@DataServerMapper(dataSource = JkDataSource.class)
@Repository
public interface JkAccountMapper extends BaseMapper<JkAccount> {


}
