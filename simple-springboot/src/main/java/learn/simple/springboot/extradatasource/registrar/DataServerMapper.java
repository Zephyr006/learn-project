package learn.simple.springboot.extradatasource.registrar;


import learn.simple.springboot.extradatasource.datasource.AbstractDataSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface DataServerMapper {

    /**
     * 属于哪个数据源
     * @return
     */
    Class<? extends AbstractDataSource> dataSource();

}
