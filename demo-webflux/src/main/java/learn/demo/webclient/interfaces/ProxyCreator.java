package learn.demo.webclient.interfaces;

/**
 * 创建代理对象的接口抽象，方便扩展更多的代理实现，如基于JdkProxy、Cglib等的代理实现
 *
 * @author Zephyr
 * @since 2020-11-22.
 */
public interface ProxyCreator {

    /**
     * 创建代理对象
     * @param type 要创建的代理对象的类型
     */
    Object create(Class<?> type);
}
