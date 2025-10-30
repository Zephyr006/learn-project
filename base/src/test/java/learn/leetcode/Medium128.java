package learn.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * 128. 最长连续序列
 * https://leetcode.cn/problems/longest-consecutive-sequence/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium128 {
    public static void main(String[] args) {
        int res = new Medium128().longestConsecutive(
                LeetcodeHelper.toIntArray("[100,4,200,1,3,2]"));
        System.out.println(res);
    }

    /**
     * 哈希：注意 本题解答的两个关键点
     * - 避免重复处理相同值元素 & 减少for循环从小到大遍历的次数
     */
    public int longestConsecutive(int[] nums) {
        if (nums.length < 2) {
            return nums.length;
        }
        Set<Integer> set = new HashSet<>();
        for (int n : nums) {
            set.add(n);
        }

        int result = 1;
        // ！ 这里要遍历 set，避免重复处理相同值的元素
        for (int n : set) {
            // ！for循环遍历是从小到大，所以如果存在比当前值小的，说明后面循环遍历会处理，
            // 这里跳过即可，可以避免 for 循环调用
            if (set.contains(n - 1)) {
                continue;
            }

            int count = 1;
            for (int i = n + 1; i < 1000000000; i++) {
                if (set.contains(i)) {
                    count++;
                } else {
                    break;
                }
            }
            result = Math.max(count, result);
        }
        return result;
    }
}
