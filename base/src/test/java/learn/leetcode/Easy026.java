package learn.leetcode;

import java.util.Arrays;

/**
 * 217. 存在重复元素
 *
 * 给你一个整数数组 nums 。如果任一值在数组中出现 至少两次 ，返回 true ；如果数组中每个元素互不相同，返回 false 。
 *  
 *
 * 示例 1：
 *
 * 输入：nums = [1,2,3,1]
 * 输出：true
 * 示例 2：
 *
 * 输入：nums = [1,2,3,4]
 * 输出：false
 * 示例 3：
 *
 * 输入：nums = [1,1,1,3,3,4,3,2,4,2]
 * 输出：true
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/contains-duplicate
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2022/3/13.
 */
public class Easy026 {

    public static void main(String[] args) {
        boolean b = new Easy026().containsDuplicate(new int[]{2,14,18,22,22});
        System.out.println(b);
    }

    public boolean containsDuplicate(int[] nums) {
        Arrays.sort(nums);
        // 这里可以优化，由于只需要判断是否存在重复，则：只需要判断当前元素值与当前元素的下一个值是否相等即可
        // 要注意，采用优化的判断逻辑需要控制下标 i < n - 1，否则数组下标越界
        int count = 1;
        int current = nums[0];
        for (int i = 1; i < nums.length; i++) {
            if (current == nums[i]) {
                count++;
                if (count >= 2)
                    return true;
            } else {
                current = nums[i];
                count = 1;
            }
        }
        return false;
    }


}
