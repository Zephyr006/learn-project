package learn.leetcode;

/**
 * 9. 回文数
 *
 * 判断一个整数是否是回文数。回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。
 *
 * 示例 1:
 *
 * 输入: 121
 * 输出: true
 * 示例 2:
 *
 * 输入: -121
 * 输出: false
 * 解释: 从左向右读, 为 -121 。 从右向左读, 为 121- 。因此它不是一个回文数。
 * 示例 3:
 *
 * 输入: 10
 * 输出: false
 * 解释: 从右向左读, 为 01 。因此它不是一个回文数。
 * 进阶:
 *
 * 你能不将整数转为字符串来解决这个问题吗？
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/palindrome-number
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @author Zephyr
 * @date 2020/7/3.
 */
public class Easy004 {

    public static void main(String[] args) {
        long dev = (long)Math.pow(10, (int)Math.log10(112));
        System.out.println(dev);

        System.out.println(new Easy004().isPalindrome(1221));
    }

    public boolean isPalindrome(int x) {
        //if (x < 0)
        //    return false;

        // 特殊情况：如上所述，当 x < 0 时，x 不是回文数。
        // 同样地，如果数字的最后一位是 0，为了使该数字为回文，则其第一位数字也应该是 0
        // 只有 0 满足这一属性
        if (x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }

        // 第一种解法：将x转换为字符串，判断是否回文
        //String str = String.valueOf(x);
        //for (int i = 0; i < str.length()/2; i++) {
        //    char left = str.charAt(i);
        //    char right = str.charAt(str.length() - i - 1);
        //
        //    if (left != right)
        //        return false;
        //}
        //return true;


        //第二种解法：不转换为字符串，直接以数字的形式判断是否回文
        int dev = 1;
        while (x / dev >= 10) {
            dev *= 10;
        }

        /*  循环条件的初级版本
        double times = Math.log10(x) / 2;
        for (int i = 0; i < times; i++) {
            int left = x / dev;
            int right = x % 10;
            if (left != right)
                return false;
            // 去掉x的最高位和最低位，先去掉最高位
            x = x % dev;
            x /= 10;
            dev /= 100;
        }*/

        //循环条件的优化版本
        while (x > 0) {
            int left = x / dev;
            int right = x % 10;
            if (left != right)
                return false;
            // 去掉x的最高位和最低位，先去掉最高位
            x = x % dev;
            x /= 10;
            dev /= 100;
        }
        return true;
    }

}
