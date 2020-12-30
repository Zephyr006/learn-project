package learn.light4j.domain.subscribe.request;

import lombok.Data;

/**
 * @author: cao
 * @time: 2020/12/7 2:14 下午
 */
@Data
public class SubscribeCriteria {

    /**
     * 中台userId
     */
    private String dataCenterId;

    /**
     * app后台userId
     */
    private String appUserId;
}
