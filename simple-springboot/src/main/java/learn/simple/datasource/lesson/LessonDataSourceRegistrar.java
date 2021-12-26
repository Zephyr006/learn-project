package learn.simple.datasource.lesson;


import learn.simple.springboot.extradatasource.registrar.AbstractDataSourceRegistrar;

/**
 * @author Zephyr
 * @date 2021/12/26.
 */
public class LessonDataSourceRegistrar extends AbstractDataSourceRegistrar {


    @Override
    protected String getJavaMapperLocation() {
        return "learn/simple/datasource/lesson/mapper";
    }

}
