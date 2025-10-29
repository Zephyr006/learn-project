package learn.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 560. 和为 K 的子数组
 * https://leetcode.cn/problems/subarray-sum-equals-k/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium560 {
    /**
     * 前缀和：使用 map 保存前缀和， 使用前缀和缓存子数组结果，避免重复计算
     */
    public int subarraySum(int[] nums, int k) {
        // map 中 key：从下标 0 开始的子数组和； val：子数组和出现的次数
        Map<Integer, Integer> map = new HashMap<>();
        // 问：为什么要把 s[0]=0 也加到哈希表中？
        // 答：举个最简单的例子，nums=[1], k=1。如果不把 s[0]=0 加到哈希表中，按照我们的算法，没法算出这里有 1 个符合要求的子数组。
        // 也可以这样理解，要想把任意子数组都表示成两个前缀和的差，必须添加 s[0]=0，否则当子数组是前缀时，没法减去一个数，
        // 具体见 前缀和及其扩展 中的讲解。
        map.put(0, 1);

        // preSum 为从下表 0 到当前位置的和
        int preSum = 0;
        int result = 0;
        for  (int i = 0; i < nums.length; i++) {
            preSum += nums[i];
            if (map.containsKey(preSum - k)) {
                result += map.get(preSum - k);
            }
            map.compute(preSum, (key,val) -> val == null ? 1 : val + 1);
        }
        return result;
    }
}
