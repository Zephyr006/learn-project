package learn.leetcode;

/**
 * 1493. 删掉一个元素以后全为 1 的最长子数组
 * 给你一个二进制数组 nums ，你需要从中删掉一个元素。
 *
 * 请你在删掉元素的结果数组中，返回最长的且只包含 1 的非空子数组的长度。
 *
 * 如果不存在这样的子数组，请返回 0 。
 *
 * https://leetcode.cn/problems/longest-subarray-of-1s-after-deleting-one-element/description/
 */
public class Medium1493 {

    public static void main(String[] args) {
        int count = new Medium1493().longestSubarray(LeetcodeHelper.toIntArray("[1,1,0,1]"));
        assert count == 3;
        count = new Medium1493().longestSubarray(LeetcodeHelper.toIntArray("[1,1,1]"));
        assert count == 2;
        count = new Medium1493().longestSubarray(LeetcodeHelper.toIntArray("[1,0,0,0,0]"));
        assert count == 1;
    }

    public int longestSubarray(int[] nums) {
        int count0 = 0; //窗口中 0 的个数
        int max = 0;
        for (int left =0, right = 0; right < nums.length; right++) {
            // 窗口右侧遇到元素值 0
            count0 = nums[right] == 0 ? count0 + 1 : count0;
            // 滑动窗口中元素值 0 的个数超过一个，需要移动滑动窗口的左边界，直至滑动窗口中只剩下一个元素值 0
            while (count0 > 1) {
                count0 = nums[left] == 0 ? count0 - 1 : count0;
                left++;
            }
            // 记录当前窗口中的元素值数量，注意不是 right-left+1，因为我们要删掉一个数
            max = Math.max(max, right - left);
        }
        return max;
    }

    /**
     * 把原始数组转化为元素 1 的个数的计数数组，然后计算定长窗口为 2 的最大和，就是删掉一个元素后全为 1 的最长子数组
     */
//     public int longestSubarray(int[] nums) {
//         List<Integer> list = new ArrayList<>();
//         int count = 0;
//         for (int num : nums) {
//             // 进入元素值为 1 的子数组
//             if (num == 1) {
//                 count++;
//                 // 刚离开元素值为 1 的子数组，要记录元素 1 的个数
//             } else if (count > 0) {
//                 list.add(count);
//                 count = 0;
//                 // 连续的元素值不为 1
//             } else {
//                 list.add(Integer.MIN_VALUE);
//             }
//         }
//         if (count > 0) {
//             list.add(count);
//         }
//
//         // 要处理整个数组都是 1 的情况，也就是 list 中只有一个元素
//         int max =  0;
//         for (int i = 1; i < list.size(); i++) {
//             max = Math.max(max, list.get(i) + list.get(i - 1));
//         }
//         return max;
//     }


}
