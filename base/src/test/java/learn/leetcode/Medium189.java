package learn.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 189. 轮转数组
 * https://leetcode.cn/problems/rotate-array/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium189 {
    public static void main(String[] args) {
        new Medium189().rotate(LeetcodeHelper.toIntArray("[1,2,3,4,5,6]"), 1);
    }

    /**
     * 数组：把下标 k ~ 2k 的元素暂存到 map 中，然后直接把数组最前面的元素复制过去
     */
    public void rotate(int[] nums, int k) {
        k = k % nums.length;
        if (k == 0) {
            return;
        }

        // 把前 k 个元素向后移动 k 位，对应位置的元素存入 map
        int len = nums.length;
        Map<Integer, Integer> idxMap = new HashMap<>(k);
        for (int i = 0; i < k; i++) {
            int idx = (i + k) % len;
            idxMap.put(idx, nums[idx]);
            nums[idx] = nums[i];
        }

        // 开始位置：k-1 ，指针向左移动，在 k ~ 2k 之间的元素在前面已经处理过，这里不需要处理
        int newIdx = k - 1, count = 0;
        for (; count < nums.length - k; newIdx--) {
            // 如果指针向左移动的过程中出现越界( <0 )，则从数组的左右侧继续遍历
            if (newIdx < 0) {
                newIdx += nums.length;
            }
            // 如果 map 中包含当前下标，说明前面已经处理过，跳过
            if (idxMap.containsKey(newIdx)) {
                continue;
            }
            // 当前是按照新的下标位置遍历的，需要找到元素移动之前的下标位置，然后把对应值放到新的位置
            // (oldIdx + k) % len = newIdx
            // oldIdx = (newIdx - k + len) % len
            Integer oldIdx = (newIdx - k + len) % len;
            nums[newIdx] = idxMap.getOrDefault(oldIdx, nums[oldIdx]);
            count++;
        }

        LeetcodeHelper.print(nums);
    }
}
