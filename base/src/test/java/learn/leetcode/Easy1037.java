package learn.leetcode;

/**
 *
 * 1137. 第 N 个泰波那契数
 *
 * 泰波那契序列 Tn 定义如下： 
 *
 * T0 = 0, T1 = 1, T2 = 1, 且在 n >= 0 的条件下 Tn+3 = Tn + Tn+1 + Tn+2
 *
 * 给你整数 n，请返回第 n 个泰波那契数 Tn 的值。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：n = 4
 * 输出：4
 * 解释：
 * T_3 = 0 + 1 + 1 = 2
 * T_4 = 1 + 1 + 2 = 4
 * 示例 2：
 *
 * 输入：n = 25
 * 输出：1389537
 *  
 *
 * 提示：
 *
 * 0 <= n <= 37
 * 答案保证是一个 32 位整数，即 answer <= 2^31 - 1。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/n-th-tribonacci-number
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2021/8/8.
 */
public class Easy1037 {

    public static void main(String[] args) {
        int tribonacci = new Solution().tribonacci(4);
        System.out.println(tribonacci);
        int tribonacci2 = new Solution().tribonacci2(4);
        System.out.println(tribonacci);

        for (int i = 0; i < 38; i++) {
            if (new Solution().tribonacci(i) != new Solution().tribonacci2(i)) {
                System.err.println(i);
                break;
            }
        }

    }

    // 思路：动态规划
    static class Solution {
        // 第一次解题，使用数据保存之前的计算结果，暴力计算
        public int tribonacci(int n) {
            int[] tn = new int[38];
            tn[0] = 0;
            tn[1] = 1;
            tn[2] = 1;
            if (n < 3) {
                return tn[n];
            }

            for (int i = 2; i < n; i++) {
                tn[i + 1] = nextTriboacci(tn[i - 2], tn[i - 1], tn[i]);
            }
            return tn[n];
        }

        // 第二次的借鉴思路，使用临时变量（取代数组）保存计算的值，减少内存占用
        public int tribonacci2(int n) {
            if (n == 0) {
                return 0;
            } else if (n < 3) {
                return 1;
            }

            int t1, t2 = 0, t3 = 1, t4 = 1;
            for (int i = 2; i < n; i++) {
                t1 = t2;
                t2 = t3;
                t3 = t4;
                t4 = t1 + t2 + t3;
            }
            return t4;
        }

        int nextTriboacci(int t1, int t2, int t3) {
            return t1 + t2 + t3;
        }
    }
}
