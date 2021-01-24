package learn.datasource.registrar;

import learn.datasource.datasource.AbstractDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author: caoyanan
 * @time: 2020/11/2 7:00 下午
 */
public class DataServerMapperFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    private Class<? extends AbstractDataSource> dataSource;

    private final Class<T> mapperClass;

    private ApplicationContext applicationContext;

    public DataServerMapperFactoryBean(Class<T> mapperInterface) {
        this.mapperClass = mapperInterface;
    }

    /**
     * spring构建bean时会调用
     * @param dataSource
     */
    public void setDataSource(Class<? extends AbstractDataSource> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public T getObject() {
        AbstractDataSource bean = applicationContext.getBean(dataSource);
        return bean.getMapperInstance(mapperClass);
    }

    @Override
    public Class<?> getObjectType() {
        return mapperClass;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
