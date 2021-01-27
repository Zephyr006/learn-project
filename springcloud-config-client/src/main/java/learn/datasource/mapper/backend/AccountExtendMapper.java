package learn.datasource.mapper.backend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.datasource.GkDataSource;
import learn.datasource.entity.AccountExtend;
import learn.datasource.registrar.DataServerMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Zephyr
 * @date 2021/1/15.
 */
@Repository
@DataServerMapper(dataSource = GkDataSource.class)
public interface AccountExtendMapper extends BaseMapper<AccountExtend> {

    default AccountExtend findByUid(Long uid) {
        if (uid != null) {
            return selectOne(Wrappers.<AccountExtend>lambdaQuery().eq(AccountExtend::getUid, uid).last(" limit 1"));
        }
        return null;
    }
}
