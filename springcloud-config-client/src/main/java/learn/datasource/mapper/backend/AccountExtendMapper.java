package learn.datasource.mapper.backend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.entity.AccountExtend;
import learn.datasource.registrar.DataServerMapper;

/**
 * @author Zephyr
 * @date 2021/1/15.
 */
@DataServerMapper
public interface AccountExtendMapper extends BaseMapper<AccountExtend> {

    default AccountExtend findByUid(Long uid) {
        if (uid != null) {
            return selectOne(Wrappers.<AccountExtend>lambdaQuery().eq(AccountExtend::getUid, uid).last(" limit 1"));
        }
        return null;
    }
}
