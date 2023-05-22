package learn.leetcode;

/**
 * 67. 二进制求和
 *
 * 给你两个二进制字符串，返回它们的和（用二进制表示）。
 *
 * 输入为 非空 字符串且只包含数字 1 和 0。
 *
 *  
 *
 * 示例 1:
 *
 * 输入: a = "11", b = "1"
 * 输出: "100"
 * 示例 2:
 *
 * 输入: a = "1010", b = "1011"
 * 输出: "10101"
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/add-binary
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2020/7/8.
 */
public class Easy016 {

    /**
     * 模拟计算法：从右到左，逐位计算，并记录进位标志位carry
     * @param a
     * @param b
     * @return
     */
    public String addBinary(String a, String b) {
        int carry = 0;   //进位标志位
        int radix = 2;   //进制： 二进制

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < Math.max(a.length(), b.length()); i++) {
            int ia = i >= a.length() ? 0 : a.charAt(a.length() -1 - i) - '0';
            int ib = i >= b.length() ? 0 : b.charAt(b.length() -1 - i) - '0';

            int tempSum = ia + ib + carry;
            if (tempSum >= radix) {
                result.insert(0, tempSum - radix);
                carry = 1;
            } else {
                result.insert(0, tempSum);
                carry = 0;
            }
        }
        if (carry > 0)
            result.insert(0, carry);
        return result.toString();
    }

    public static void main(String[] args) {

        new Easy016().addBinary("101", "1");
    }
}
