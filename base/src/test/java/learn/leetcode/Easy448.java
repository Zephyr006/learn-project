package learn.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 448. 找到所有数组中消失的数字
 * { @link https://leetcode.cn/problems/find-all-numbers-disappeared-in-an-array/}
 *
 * @author Zephyr
 * @date 2023-08-15
 */
public class Easy448 {

    public static void main(String[] args) {
        int[] t = new int[]{4, 3, 2, 7, 8, 2, 3, 1};
        System.out.println(new Solution().findDisappearedNumbers(t));
    }

    static class Solution {
        public List<Integer> findDisappearedNumbers(int[] nums) {
            for (int i = 0; i < nums.length; i++) {
                // nums[i] - 1 表示当前位置的数字应该出现的下标，比如示例中出现在位置0的数字4，其应该出现在下标3，如果下标3的对应数字不是4，则交换这两个位置的数字
                // 这里必须是while,如果用if则数据错误,具体原因需要调试研究
                while (nums[i] != i + 1 && nums[i] != nums[nums[i] - 1]) {
                    swap(nums, i, nums[i] - 1);
                }
            }

            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < nums.length; i++) {
                if (nums[i] != i + 1) {
                    result.add(i + 1);
                }
            }

            return result;
        }

        void swap(int[] nums, int i, int j) {
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
    }
}
