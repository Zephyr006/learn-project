package learn.demo.webclient.interfaces;

import learn.demo.webclient.beans.MethodInfo;

/**
 * http请求处理
 * @author Zephyr
 * @since 2020-11-28.
 */
public interface RestHandler {


    /**
     * 根据要请求的方法信息发送rest请求，并构建返回值
     */
    Object invokeRest(MethodInfo methodInfo);

}
