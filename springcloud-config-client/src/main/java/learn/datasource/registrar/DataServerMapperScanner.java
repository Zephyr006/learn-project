/**
 * Copyright 2010-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package learn.datasource.registrar;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

public class DataServerMapperScanner extends ClassPathBeanDefinitionScanner {

  public DataServerMapperScanner(BeanDefinitionRegistry registry) {
    super(registry, false);
  }


  @Override
  public Set<BeanDefinitionHolder> doScan(String... basePackages) {
    Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

    if (!beanDefinitions.isEmpty()) {
      processBeanDefinitions(beanDefinitions);
    }

    return beanDefinitions;
  }

  private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
    ScannedGenericBeanDefinition beanDefinition;
    for (BeanDefinitionHolder holder : beanDefinitions) {
      beanDefinition = (ScannedGenericBeanDefinition) holder.getBeanDefinition();


      MergedAnnotation<DataServerMapper> mergedAnnotation = beanDefinition.getMetadata()
              .getAnnotations().get(DataServerMapper.class);
      Optional<String> dataSource = mergedAnnotation.getValue("dataSource", String.class);

      String beanClassName = beanDefinition.getBeanClassName();
      if (beanClassName != null) {
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
      }
      if (dataSource.isPresent()) {
        try {
          beanDefinition.getPropertyValues().add("dataSource",
                  Class.forName(dataSource.get()));
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }

      beanDefinition.setBeanClass(DataServerMapperFactoryBean.class);

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
    return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
  }


  public void registerFilters(Class<? extends Annotation> annotationClass) {
    super.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
  }

}
