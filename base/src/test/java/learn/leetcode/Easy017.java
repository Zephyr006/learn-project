package learn.leetcode;

/**
 * 69. x 的平方根
 *
 * 实现 int sqrt(int x) 函数。
 *
 * 计算并返回 x 的平方根，其中 x 是非负整数。
 *
 * 由于返回类型是整数，结果只保留整数的部分，小数部分将被舍去。
 *
 * 示例 1:
 *
 * 输入: 4
 * 输出: 2
 * 示例 2:
 *
 * 输入: 8
 * 输出: 2
 * 说明: 8 的平方根是 2.82842...,
 *      由于返回类型是整数，小数部分将被舍去。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sqrtx
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @since 2020-07-08.
 */
public class Easy017 {

    /**
     * 二分查找
     * 注意的点：
     *   循环条件：         while (left <= right)
     *   折半时middle的值： middle = left + (right - left) / 2
     *   求平方的计算结果应该用long类型接收：long pow = (long) Math.pow(middle, 2);
     *   由于我们所有的运算都是整数运算，不存在误差，因此在得到最终的答案 answer 后，也就不需要再去尝试 answer+1 了
     * @param x
     * @return
     */
    public int mySqrt(int x) {
        int left=0, right = x;
        int answer = -1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            long pow = (long) Math.pow(middle, 2);  //平方值应该用long类型接收，否则可能会移除
            if (pow <= x) {
                answer = middle;
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        new Easy017().mySqrt(8);
    }
}
