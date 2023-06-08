package learn.springboot.extradatasource.registrar;

import learn.springboot.extradatasource.datasource.AbstractDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class DataServerMapperFactoryBean<T> implements FactoryBean<T>, BeanFactoryAware {

    private Class<? extends AbstractDataSource> dataSource;

    private final Class<T> mapperClass;

    private BeanFactory beanFactory;

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
        AbstractDataSource bean = beanFactory.getBean(dataSource);
        if (bean.register())  {
            return bean.getMapperInstance(mapperClass);
        }

        try {
            Object mockMapperImpl = Proxy.newProxyInstance(
                    mapperClass.getClassLoader(), new Class[]{mapperClass}, new InvokeHandler());
            return (T)mockMapperImpl;
        } catch (Exception ex) {
            throw new RuntimeException("动态创建mapper接口模拟类失败");
        }
    }

    @Override
    public Class<?> getObjectType() {
        return mapperClass;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public static class InvokeHandler implements InvocationHandler {

        /**
         * MockMapperImpl的实现
         * @param proxy
         * @param method
         * @param args
         * @return
         * @throws Throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            log.error("mock查询 {}::{} ，此mapper没有配置数据源，代理实现将返回空结果",
                    method.getDeclaringClass().getName(), method.getName());
            return null;
        }
    }
}
