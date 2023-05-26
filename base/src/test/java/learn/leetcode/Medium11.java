package learn.leetcode;

/**
 * @author Zephyr
 * @since 2022-03-15.
 */
public class Medium11 {

    public static void main(String[] args) {

    }

    public int countMaxOrSubsets(int[] nums) {
        int n = nums.length, mask = 1 << n;
        int max = 0, ans = 0;
        for (int s = 0; s < mask; s++) {
            int cur = 0;
            for (int i = 0; i < n; i++) {
                if (((s >> i) & 1) == 1) cur |= nums[i];
            }
            if (cur > max) {
                max = cur; ans = 1;
            } else if (cur == max) {
                ans++;
            }
        }
        return ans;
    }
}
