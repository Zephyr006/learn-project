package learn.datasource;

import learn.datasource.registrar.DataServerRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: caoyanan
 * @time: 2020/11/2 4:44 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({DataServerRegistrar.class})
public @interface EnableDataServer {
}
