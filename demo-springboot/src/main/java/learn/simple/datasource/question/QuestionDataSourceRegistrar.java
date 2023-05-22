package learn.simple.datasource.question;


import learn.simple.springboot.extradatasource.registrar.AbstractDataSourceRegistrar;

/**
 * 用于将java中的mapper文件解析为bean，重点是实现了ImportBeanDefinitionRegistrar接口
 *
 * @author Zephyr
 * @date 2021/12/26.
 */
public class QuestionDataSourceRegistrar extends AbstractDataSourceRegistrar {


    @Override
    protected String getJavaMapperLocation() {
        return "learn/simple/datasource/question/mapper";
    }

}
