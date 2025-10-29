package learn.leetcode;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 373. 查找和最小的 K 对数字
 * https://leetcode.cn/problems/find-k-pairs-with-smallest-sums/description/?envType=study-plan-v2&envId=top-interview-150
 */
public class Medium373 {
    public static void main(String[] args) {
        List<List<Integer>> res = new Medium373().kSmallestPairs(
                LeetcodeHelper.toIntArray("[1,2,4,5,6]"), LeetcodeHelper.toIntArray("[3,5,7,9]"), 3);
        System.out.println(res);
    }

    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> res = new LinkedList<>();
        LinkedList<int[]> queue = new LinkedList<>();
        queue.add(new int[]{0, 0});
        // int count = k;
        int idx1 = 0, idx2 = 0;
        while (res.size() < k) {
            // 队手元素为最小，直接放入结果集
            int[] arr = queue.removeFirst();
            res.add(Arrays.asList(nums1[arr[0]], nums2[arr[1]]));

            // 如果还不足 k 个结果，需要加载更多到队列中，直到够 k 个
            if (queue.isEmpty() && res.size() < k) {
                idx1 = arr[0];
                idx2 = arr[1];

                if (nums1[idx1] + nums2[idx2 + 1] == nums1[idx1 + 1] + nums2[idx2]) {
                    queue.add(new int[]{idx1, idx2 + 1});
                    queue.add(new int[]{idx1 + 1, idx2});
                } else {
                    int max = Math.max(nums1[idx1] + nums2[idx2 + 1], nums1[idx1 + 1] + nums2[idx2]);
                    while (k - res.size() - queue.size() > 0 && idx1 < nums1.length - 1 && idx2 < nums2.length - 1) {
                        if (nums1[idx1] + nums2[idx2 + 1] < max) {
                            queue.add(new int[]{idx1, ++idx2});
                        } else if (nums1[idx1 + 1] + nums2[idx2] < max) {
                            queue.add(new int[]{++idx1, idx2});
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        return res;
    }
}
