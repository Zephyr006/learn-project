package learn.light4j.model;

/**
 * @author: caoyanan
 * @time: 2020/12/17 3:45 下午
 */
public interface ResponseCode {

    /**
     * 返回码
     * @return
     */
    String getCode();

    /**
     * 返回信息
     * @return
     */
    String getMessage();
}
