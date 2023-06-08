package learn.springboot.datasource.lesson;


import learn.springboot.extradatasource.registrar.AbstractDataSourceRegistrar;

/**
 * @author Zephyr
 * @since 2021-12-26.
 */
public class LessonDataSourceRegistrar extends AbstractDataSourceRegistrar {


    @Override
    protected String getJavaMapperLocation() {
        return "learn/simple/springboot/datasource/lesson/mapper";
    }

}
