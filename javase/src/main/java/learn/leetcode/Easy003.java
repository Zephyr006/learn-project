package learn.leetcode;

/**
 * 7. 整数反转
 * 给出一个 32 位的有符号整数，你需要将这个整数中每位上的数字进行反转。
 *
 * 示例 1:
 *
 * 输入: 123
 * 输出: 321
 *  示例 2:
 *
 * 输入: -123
 * 输出: -321
 * 示例 3:
 *
 * 输入: 120
 * 输出: 21
 * 注意:
 *
 * 假设我们的环境只能存储得下 32 位的有符号整数，则其数值范围为 [−231,  231 − 1]。请根据这个假设，如果反转后整数溢出那么就返回 0。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/reverse-integer
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @author Zephyr
 * @date 2020/7/1.
 */
public class Easy003 {

    public static void main(String[] args) {
        System.out.println(new Easy003().reverse(Integer.MAX_VALUE));
    }

    /**
     *
     * @param x
     * @return
     */
    public int reverse(int x) {
        //result的变量类型声明为long， 如果反转后结果产生溢出，则强转为int后其值会发生变化（高位被舍弃）
        long result = 0;
        //（十进制）常规数据，从个位开始处理，逐位取出，每次取出的数字都将result整体向左移动一位，
        //就是将原始数据从右向左逐位取出，然后向result的尾部（右侧）追加
        while (x != 0) {
            result = result * 10 + x % 10;
            x = x / 10;
        }
        return  (int) result == result ? (int) result : 0;
    }
}
