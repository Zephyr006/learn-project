package learn.example.javase;

import java.util.concurrent.TimeUnit;

/**
 * @author Zephyr
 * @date 2020/8/11.
 */
public class SleepUtil {

    /**
     * sleep 默认单位：毫秒
     * @param mills
     */
    public static void sleep(long mills) {
        SleepUtil.sleep(mills, TimeUnit.MILLISECONDS);
    }

    public static void sleep(long timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
