package learn.datasource.registrar;

import learn.datasource.datasource.AbstractDataSource;
import learn.datasource.datasource.QuestionDataSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: caoyanan
 * @time: 2020/11/2 5:31 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DataServerMapper {

    /**
     * 属于哪个数据源
     * @return
     */
    Class<? extends AbstractDataSource> dataSource() default QuestionDataSource.class;

}
