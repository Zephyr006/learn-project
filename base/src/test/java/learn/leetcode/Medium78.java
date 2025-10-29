package learn.leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 78. 子集
 * https://leetcode.cn/problems/subsets/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium78 {
    public static void main(String[] args) {
        List<List<Integer>> list = new Medium78().subsets(new int[]{0,1});
        System.out.println(list);
    }

    /**
     * 回溯：数组中的每一个数字都有 “使用”和“不使用”两种情况
     */
    List<List<Integer>> result = new ArrayList<>();
    public List<List<Integer>> subsets(int[] nums) {
        dfs(nums, 0, new LinkedList<>());
        return result;
    }

    private void dfs(int[] nums, int idx, LinkedList<Integer> path) {
        if (idx == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        // 分为两种情况：使用当前下标的元素、不使用当前下标的元素
        dfs(nums, idx + 1, path);

        path.add(nums[idx]);
        dfs(nums, idx + 1, path);
        // 回溯：删掉当前使用的元素
        path.removeLast();
    }
}
