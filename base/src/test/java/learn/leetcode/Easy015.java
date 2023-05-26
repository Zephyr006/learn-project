package learn.leetcode;

/**
 * 66. 加一
 *
 * 给定一个由整数组成的非空数组所表示的非负整数，在该数的基础上加一。
 *
 * 最高位数字存放在数组的首位， 数组中每个元素只存储单个数字。
 *
 * 你可以假设除了整数 0 之外，这个整数不会以零开头。
 *
 * 示例 1:
 *
 * 输入: [1,2,3]
 * 输出: [1,2,4]
 * 解释: 输入数组表示数字 123。
 * 示例 2:
 *
 * 输入: [4,3,2,1]
 * 输出: [4,3,2,2]
 * 解释: 输入数组表示数字 4321。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/plus-one
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @since 2020-07-08.
 */
public class Easy015 {

    /**
     * 初始想法：每次在当前位 +1 并记录进位情况 ‘carry’，在计算高位时加上进位carry的值
     * 这种写法适用于  +1 ~ +9     （题目仅要求 +1）
     * 优化：如果计算个位时没有触发进位，则数组末位加一后直接返回数组即可！
     * 再优化：不论何时，只要不再涉及到进位，就可以返回数组，不再向下处理
     */
    public int[] plusOne(int[] digits) {
        int carry = 0;  //是否进位
        int sum = 1;    ///题目的要求：“在该数的基础上加一”，这里的sum为初始要加的 “1”
        for (int i = digits.length-1; i >= 0; i--) {
            sum = digits[i] + carry + sum;
            if (sum >= 10) {
                carry = 1;
                digits[i] = sum - 10;
            }
            // 只要当前位的计算不涉及到进位，则高位数组已经被不需要额外处理，直接返回即可
            else {
                digits[i] = sum;
                return digits;
            }
            sum = 0;
        }
        if (carry == 1) {
            int[] result = new int[digits.length + 1];
            result[0] = carry;
            // 如果是按照题中的要求，只是+1则不需要copy数组，
            // 因为carry==1意味着原始数组是n个9，计算结果必然为：第一位是1，后面各位都是0
            //System.arraycopy(digits, 0, result, 1, digits.length);
            return result;
        }
        // 按照题目中的要求，程序永远不会运行到这里
        return digits;
    }


    public static void main(String[] args) {
        new Easy015().plusOne(new int[]{9,9,9,});
    }
}
