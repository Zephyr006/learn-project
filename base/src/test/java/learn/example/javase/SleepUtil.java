package learn.example.javase;

import java.util.Random;
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

    public static boolean randomSleep(int atLeast, int bound, TimeUnit timeUnit) {
        timeUnit = (timeUnit == null) ? TimeUnit.MILLISECONDS : timeUnit;
        atLeast = Math.toIntExact(timeUnit.toMillis(atLeast));
        bound = Math.toIntExact(timeUnit.toMillis(bound));

        Random random = new Random();
        while (true) {
            int i = random.nextInt(bound);
            if (i > atLeast) {
                System.out.println(Thread.currentThread().getName() + " 线程将睡眠 " + (i / 1000) + " 秒 ...");
                SleepUtil.sleep(i);
                return true;
            }
        }
    }
}
