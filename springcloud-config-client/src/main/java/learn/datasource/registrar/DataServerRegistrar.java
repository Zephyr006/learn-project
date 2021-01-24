package learn.datasource.registrar;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author: caoyanan
 * @time: 2020/11/2 4:45 下午
 */
public class DataServerRegistrar implements ImportBeanDefinitionRegistrar {

    public static final String MAPPER_INTERFACE_PACKAGE = "learn.datasource.mapper";

    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata,
            BeanDefinitionRegistry registry) {

        registerMapper(registry);
    }

    private void registerMapper(BeanDefinitionRegistry registry) {

        DataServerMapperScanner scanner = new DataServerMapperScanner(registry);
        scanner.registerFilters(DataServerMapper.class);
        scanner.scan(MAPPER_INTERFACE_PACKAGE);
    }

}