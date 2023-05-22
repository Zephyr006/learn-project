package learn.simple.springboot.extradatasource.registrar;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 作用：动态注入Bean
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @author Zephyr
 * @date 2021/12/25.
 */
public abstract class AbstractDataSourceRegistrar implements ImportBeanDefinitionRegistrar {

    /**
     * 每个数据源的xml路径必须在不同路径下，否则只能都加载，无法选择性加载
     */
    protected abstract String getJavaMapperLocation();

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        DataServerMapperScanner scanner = new DataServerMapperScanner(registry);
        scanner.registerFilters(DataServerMapper.class);
        // 扫描指定的基本包路径（当前路径及其子文件夹）
        scanner.scan(getJavaMapperLocation());
    }

}
