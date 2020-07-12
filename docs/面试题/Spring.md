## spring的生命周期

1. bean定义：在配置文件里面用<bean></bean>来进行定义。

1. bean初始化：A.在配置文件中通过指定init-method属性来完成   B.实现org.springframwork.beans.factory.InitializingBean接口

3. bean调用：有三种方式可以得到bean实例，并进行调用

4. bean销毁：A.使用配置文件指定的destroy-method属性   B.实现org.springframwork.bean.factory.DisposeableBean接口

> Spring 只帮我们管理单例模式 Bean 的**完整**生命周期，对于 prototype 的 bean ，Spring 在创建好交给使用者之后则不会再管理后续的生命周期。

**详细描述：**

> 实例化 => 填充属性 => 调用`BeanNameAware`的setBeanName方法 => 调用`BeanFactoryAware`的setBeanFactory方法 => 调用`ApplicationContextAware`的setApplicationContext方法 => 调用`BeanPostProcess`的postProcessBeforeInitialization方法 => 调用`InitializationBean`的afterPropertiesSet方法 => 调用定制的初始化方法 => 调用BeanPostProcess的postProcessAfterInitialization方法 => Bean准备完毕 => 调用DisposeableBean的destory方法 => 调用自定义的销毁方法

**Bean的作用域：**

singleton：这种bean范围是默认的，这种范围确保不管接受到多少个请求，每个容器中只有一个bean的实例，单例的模式由bean factory自身来维护。

prototype：每次调用Bean时，容器都返回一个新的实例（即每次调用getBean时，都相当于执行new XxxBean() ）。

request：在请求bean范围内会每个Http请求创建一个新的实例，在请求完成以后，bean会失效并被垃圾回收器回收，该作用域仅适用于WebApplicationContext环境。

Session：与请求范围类似，每个session中有一个bean的实例，在session过期后，bean会随之失效，仅适用于WebApplicationContext环境。

global-session：global-session和Portlet应用相关。当你的应用部署在Portlet容器中工作时，它包含很多portlet。如果你想要声明让所有的portlet共用全局的存储变量的话，那么这全局变量需要存储在global-session中（该作用域仅适用于WebApplicationContext环境）。



## Spring中用到的设计模式

> 简单工厂：简单工厂模式的实质是由一个工厂类根据传入的参数，动态决定应该创建哪一个产品类，Spring中的BeanFactory就是简单工厂模式的体现。
>
> 工厂方法：通常由应用程序直接使用new创建新的对象，应用程序将对象的创建及初始化职责交给工厂对象。一般情况下,应用程序有自己的工厂对象来创建bean.如果将应用程序自己的工厂对象交给Spring管理,那么Spring管理的就不是普通的bean,而是工厂Bean。
>
> 单例模式：保证一个类仅有一个实例，并提供一个访问它的全局访问点。Spring中的单例模式完成了后半句话，即提供了全局的访问点BeanFactory，但没有从构造器级别去控制单例，这是因为spring管理的是是任意的java对象。Spring下默认的bean均为singleton，可以通过singleton=“true|false” 或者 scope=“？”来修改默认值
>
> 适配器：将一个类的接口转换成客户希望的另外一个接口。Adapter模式使得原本由于接口不兼容而不能一起工作的那些类可以一起工作。
>
> 包装器：动态地给一个对象添加一些额外的职责。就增加功能来说，Decorator模式相比生成子类更为灵活。Spring中用到的包装器模式在类名上有两种表现：一种是类名中含有Wrapper，另一种是类名中含有Decorator。
>
> 代理模式：为其他对象提供一种代理以控制对这个对象的访问。 从结构上来看和Decorator模式类似，但Proxy是控制，更像是一种对功能的限制，而Decorator是增加职责。spring的Proxy模式在aop中有体现，比如JdkDynamicAopProxy和Cglib2AopProxy。
>
> 观察者模式：定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。spring中Observer模式常用的地方是listener的实现。如ApplicationListener。
>
> 策略模式：定义一系列的算法，把它们一个个封装起来，并且使它们可相互替换。本模式使得算法可独立于使用它的客户而变化。Spring中在实例化对象的时候用到Strategy模式
>
> 模板方法：定义一个操作中的算法的骨架，而将一些步骤延迟到子类中。Template Method使得子类可以不改变一个算法的结构即可重定义该算法的某些特定步骤，Template Method模式一般是需要继承的。

## Spring的事务管理

###### 事务的的相关概念

> 事务就是对一系列的数据库操作进行统一的提交或回滚操作，如果插入成功，那么一起成功，如果中间有一条出现异常，那么回滚之前的所有操作，这样可以防止出现脏数据。事务具备ACID四种特性，即 Atomic（原子性）、Consistency（一致性）、Isolation（隔离性）和Durability（持久性）。
>
> 事务的隔离级别（Mysql的默认隔离级别是Repeatable read，*Oracle默认的事务隔离级别是Read committed*）：
>
> （1）read uncommited：是最低的事务隔离级别，它允许另外一个事务可以看到这个事务未提交的数据。
>
> （2）read commited：保证一个事物提交后才能被另外一个事务读取。另外一个事务不能读取该事物未提交的数据。
>
> （3）repeatable read：这种事务隔离级别可以防止脏读，不可重复读。但是可能会出现幻读。
>
> （4）serializable：这是花费最高代价但最可靠的事务隔离级别。事务被处理为顺序执行。避免了脏读，不可重复读和幻读

###### Spring的事务管理

> Spring的单机事务一般是使用TransactionMananger进行管理，Spring提供了几个关于事务处理的类：
>
> TransactionDefinition //事务属性定义
>
> TranscationStatus //代表了当前的事务，可以提交，回滚。
>
> PlatformTransactionManager //这个是spring提供的用于管理事务的基础接口，其下有一个实现的抽象类 AbstractPlatformTransactionManager,我们使用的事务管理类例如 DataSourceTransactionManager等都是这个类的子类。
>
> Spring提供的事务管理可以分为两类：编程式的和声明式的。编程式的比较灵活，但是代码量大，存在重复的代码比较多；声明式的比编程式的更灵活 (主流)。

## Spring中自动装配的方式有哪些

- **byName**： 在定义@Bean的时候，设置autowire属性为byName，那么Spring会自动寻找一个与该属性名称相同或id相同的Bean，注入进来
- **byType**： 在定义@Bean的时候，设置autowire属性为byType，那么Spring会自动寻找一个与该属性类型相同的Bean，注入进来
- **constructor（构造方法）**：与通过`byType`一样，也是通过类型查找依赖对象。区别在于它不是使用Setter方法注入，而是使用构造方法注入。如果容器中没有找到参数类型一致的bean，则抛出异常。
-  **No**：即不启用自动装配。Autowire默认的值。不使用Autowire，引用关系显式声明，spring的reference也建议不用autoware，因为这会破坏模块关系的可读性
-  **autodetect**：在byType和constructor之间自动的选择注入方式。通过bean类的自省机制（introspection）来决定是使用constructor还是byType方式进行自动装配。如果发现默认的构造器，那么将使用byType方式，否则采用 constructor。
- **default**：由上级标签<beans>的default-autowire属性确定。

**注意**： 在配置bean时，当前<bean>标签中Autowire属性的优先级比其上级标签高.

## Spring中的核心类

BeanFactory、ApplicationContext（Spring的IOC容器）：

ApplicationContext 接口继承BeanFactory接口，Spring核心工厂是BeanFactory ,BeanFactory采取延迟加载，第一次getBean时才会初始化Bean, ApplicationContext是会在加载配置文件时初始化Bean。

ApplicationContext是对BeanFactory扩展，它可以进行国际化处理、事件传递和bean自动装配以及各种不同应用层的Context实现 。

开发中基本都在使用ApplicationContext, web项目使用WebApplicationContext ，很少用到BeanFactory，详细解释见：http://wiki.jikexueyuan.com/project/spring/ioc-containers.html

## SpringMVC的执行流程

1. 用户发送请求至前端控制器DispatcherServlet

2. DispatcherServlet收到请求调用HandlerMapping处理器映射器。

3. 处理器映射器根据请求url找到具体的处理器，生成处理器对象及处理器拦截器(如果有则生成)一并返回给DispatcherServlet。

4. DispatcherServlet通过HandlerAdapter处理器适配器调用处理器

5. 执行处理器(Controller，也叫后端控制器)。

6. Controller执行完成返回ModelAndView

7. HandlerAdapter将controller执行结果ModelAndView返回给DispatcherServlet

8. DispatcherServlet将ModelAndView传给ViewReslover视图解析器

9. ViewReslover解析后返回具体View

10. DispatcherServlet对View进行渲染视图（即将模型数据填充至视图中）。

11. DispatcherServlet响应用户

