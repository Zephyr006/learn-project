package learn.javase.io.filedb.util;

/**
 * @author Zephyr
 * @date 2020/5/26.
 */
public class MathUtils {

    final static int [] sizeTable = { 9, 99, 999, 9999, 99999, 999999, 9999999,
            99999999, 999999999, Integer.MAX_VALUE };

    /**
     * 获取整数的前 prefixLength 位
     * @param x
     * @param prefixLength
     * @return
     */
    public static int getPrefix(long x, int prefixLength) {
        if (x <= sizeTable[prefixLength-1])  return (int) x;

        return (int) (Math.log10(x) + 1);
        //for (int i=0; ; i++) {
        //    if (x <= sizeTable[i]) {
        //        // 整数 x 的位数为 i+1
        //        return (int) (x /  StrictMath.pow(10, i+1 - prefixLength));
        //    }
        //}
    }

    public static int length(long x) {
        return (int) Math.log10(x) + 1;
    }

    public static int length(int x) {
        return (int) (Math.log10(x) + 1);
    }
}
