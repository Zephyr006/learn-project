package learn.demo.webclient.impl;

/**
 * 代理对象创建的接口抽象，方便扩展更多的代理实现，如基于JdkProxy、Cglib等的代理实现
 *
 * @author Zephyr
 * @date 2020/11/22.
 */
public interface ProxyCreator {

    /**
     * 创建代理对象
     * @param type 要创建的代理对象的类型
     */
    Object create(Class<?> type);
}
