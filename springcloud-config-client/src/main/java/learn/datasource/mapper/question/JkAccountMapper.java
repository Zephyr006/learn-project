package learn.datasource.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import learn.datasource.datasource.QuestionDataSource;
import learn.datasource.entity.question.JkAccount;
import learn.datasource.registrar.DataServerMapper;
import org.springframework.stereotype.Repository;

/**
 * @author Zephyr
 * @date 2021/1/21.
 */
@DataServerMapper(dataSource = QuestionDataSource.class)
@Repository
public interface JkAccountMapper extends BaseMapper<JkAccount> {


}
