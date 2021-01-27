package learn.datasource.mapper.backend;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import learn.datasource.datasource.GkDataSource;
import learn.datasource.entity.Account;
import learn.datasource.registrar.DataServerMapper;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author yangming
 * @date 2020/12/2
 */
@Repository
@DataServerMapper(dataSource = GkDataSource.class)
public interface AccountMapper extends BaseMapper<Account> {

    default List<Account> findByDataCenterIdIn(Collection<Long> dataCenterIds) {
        if (CollectionUtils.isEmpty(dataCenterIds)) {
            return Collections.emptyList();
        }
        return selectList(Wrappers.<Account>lambdaQuery()
                .in(Account::getDataCenterId, dataCenterIds));
    }

    default Account findByDataCenterId(Long dataCenterId) {
        if (dataCenterId == null) {
            return null;
        }
        return selectOne(Wrappers.<Account>lambdaQuery()
                .eq(Account::getDataCenterId, dataCenterId)
                .ne(Account::getStatus, 2).last("limit 1"));
    }

    default Account findByPhone(String phone) {
        if (phone == null || phone.length() == 0) {
            return null;
        }
        return selectOne(Wrappers.<Account>lambdaQuery()
                .eq(Account::getPhone, phone)
                .ne(Account::getStatus, 2).last("limit 1"));
    }



}
