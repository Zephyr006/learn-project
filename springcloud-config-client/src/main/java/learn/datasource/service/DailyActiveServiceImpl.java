package learn.datasource.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import learn.datasource.entity.Account;
import learn.datasource.entity.DailyActiveEntity;
import learn.datasource.entity.UserLoginInfo;
import learn.datasource.mapper.backend.AccountMapper;
import learn.datasource.mapper.backend.DailyActiveMapper;
import learn.datasource.model.UserLoginCountModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: caoyanan
 * @time: 2020/10/30 5:30 下午
 */
@Service
public class DailyActiveServiceImpl implements DailyActiveService {

    @Autowired
    private DailyActiveMapper dailyActiveMapper;

    @Autowired
    private AccountMapper accountMapper;


    @Override
    public List<DailyActiveEntity> queryDailyActive(List<Long> dataCenterId, Long beginTime, Long endTime) {

        if (CollectionUtils.isEmpty(dataCenterId)
                || Objects.isNull(beginTime)
                || Objects.isNull(endTime)) {
            return Collections.emptyList();
        }

        Date beginTimeDate = new Date(beginTime);
        Date endTimeDate = new Date(endTime);
        return dailyActiveMapper.findByDataCenterIdInAndActiveTimeBetween(
                dataCenterId, beginTimeDate, endTimeDate);
    }

    @Override
    public List<UserLoginInfo> queryUserLoginInfo(List<Long> dataCenterIds) {
        if (CollectionUtils.isEmpty(dataCenterIds)) {
            return Collections.emptyList();
        }
        List<Account> accountList = accountMapper.findByDataCenterIdIn(dataCenterIds);
        List<Long> userIdList = accountList.stream().map(Account::getId).collect(Collectors.toList());
        List<UserLoginCountModel> userLoginCountModelList = dailyActiveMapper.countByUserIds(userIdList);
        Map<Long, Integer> userLoginCountMap = userLoginCountModelList
                .stream()
                .collect(Collectors.toMap(UserLoginCountModel::getUserId, UserLoginCountModel::getLoginCount, (l, r) -> l));
        return accountList.stream()
                .map(account -> {
                    UserLoginInfo info = new UserLoginInfo();
                    info.setDataCenterId(account.getDataCenterId());
                    info.setLatestRequestTime(account.getLatestRequestTime() * 1000);
                    info.setCreateTime(account.getCreateTime().getTime());
                    Integer count = userLoginCountMap.get(account.getId());
                    info.setLoginCount(count);
                    return info;
                })
                .collect(Collectors.toList());
    }

}
