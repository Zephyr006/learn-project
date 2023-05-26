package learn.simple.datasource.question.datasource;

import learn.simple.datasource.question.config.QuestionDataSourceConfig;
import learn.simple.springboot.extradatasource.datasource.AbstractDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author Zephyr
 * @since 2021-01-12.
 */
@Component
public class QuestionDataSource extends AbstractDataSource {

    @Autowired
    public QuestionDataSource(QuestionDataSourceConfig config) {
        super(config);
    }


}
