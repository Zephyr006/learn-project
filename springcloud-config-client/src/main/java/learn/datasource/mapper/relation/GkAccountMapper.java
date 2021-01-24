package learn.datasource.mapper.relation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.datasource.datasource.RelationDataSource;
import learn.datasource.entity.relation.GkAccount;
import learn.datasource.registrar.DataServerMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Zephyr
 * @date 2021/1/21.
 */
@DataServerMapper(dataSource = RelationDataSource.class)
@Repository
public interface GkAccountMapper extends BaseMapper<GkAccount> {


}
