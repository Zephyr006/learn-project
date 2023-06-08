package learn.springboot.datasource.lesson.datasource;

import learn.springboot.datasource.lesson.config.LessonDataSourceConfig;
import learn.springboot.extradatasource.datasource.AbstractDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LessonDataSource extends AbstractDataSource {

    @Autowired
    public LessonDataSource(LessonDataSourceConfig config) {
        super(config);
    }

}
