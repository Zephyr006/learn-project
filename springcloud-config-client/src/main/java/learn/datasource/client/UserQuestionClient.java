package learn.datasource.client;

import learn.datasource.entity.gk.UserQuestion;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/18 1:48 下午
 */
public interface UserQuestionClient {


    List<UserQuestion> findByDataCenterIdAndQuestionIdIn(Long dataCenterId, List<Long> questionIds);
}
