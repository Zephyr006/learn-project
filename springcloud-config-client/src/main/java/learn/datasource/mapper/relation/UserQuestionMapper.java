package learn.datasource.mapper.relation;

import learn.datasource.datasource.RelationDataSource;
import learn.datasource.entity.relation.UserQuestion;
import learn.datasource.registrar.DataServerMapper;

import java.util.List;

/**
 * @author: caoyanan
 * @time: 2021/1/18 1:53 下午
 */
@DataServerMapper(dataSource = RelationDataSource.class)
public interface UserQuestionMapper {


    /**
     * 查询学员做的题目
     * @param tableName
     * @param dataCenterId
     * @param questionIds
     * @return
     */
    List<UserQuestion> findByDataCenterIdAndQuestionIdIn(
            String tableName, Long dataCenterId, List<Long> questionIds);
}
