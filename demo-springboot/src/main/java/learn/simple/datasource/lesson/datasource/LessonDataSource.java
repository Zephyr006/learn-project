package learn.simple.datasource.lesson.datasource;

import learn.simple.datasource.lesson.config.LessonDataSourceConfig;
import learn.simple.springboot.extradatasource.datasource.AbstractDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class LessonDataSource extends AbstractDataSource {

    @Autowired
    public LessonDataSource(LessonDataSourceConfig config) {
        super(config);
    }

}
