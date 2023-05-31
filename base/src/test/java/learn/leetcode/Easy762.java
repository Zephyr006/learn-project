package learn.leetcode;

/**
 * 762. 二进制表示中质数个计算置位
 *
 * @author Zephyr
 * @date 2022/4/5.
 */
public class Easy762 {

    public static void main(String[] args) {
        int countPrimeSetBits = new Solution().countPrimeSetBits(10, 15);
        System.out.println(countPrimeSetBits);
    }

    static class Solution {
        // 标记质数 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31
        static Integer mask = 0b010100000100010100010100010101100;
        public int countPrimeSetBits(int left, int right) {
            int ans = 0;
            for (int i = left; i <= right; i++) {
                int x = i, cnt = 0;
                // 计算置位位数
                while (x != 0){
                    // 这一步是重点
                    x -= (x & -x);
                    ++cnt;
                }
                // 判断置位位数是否为质数
                if (((1 << cnt) & mask) > 0) {
                    ans++;
                }
            }
            return ans;

        }
    }
}
