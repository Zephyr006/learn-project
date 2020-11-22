package learn.demo.webclient.impl;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于JdkProxy的代理对象生成
 *
 * @author Zephyr
 * @date 2020/11/22.
 */
public class JdkProxyCreator implements ProxyCreator {

    @Override
    public Object create(Class<?> type) {
        ServerInfo serverInfo = this.extractServerInfo(type);

        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 根据方法和参数得到调用方法的信息

                        //todo https://www.bilibili.com/video/BV1Z4411X791?p=42  12:20
                        return null;
                    }
                });
    }

    private ServerInfo extractServerInfo(Class<?> type) {
        return null;
    }

}
