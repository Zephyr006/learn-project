package learn.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 46. 全排列
 * https://leetcode.cn/problems/permutations/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium46 {

    /**
     * 回溯：用数组记录已使用过的下标
     */
    List<List<Integer>> result = new ArrayList<>();
    public List<List<Integer>> permute(int[] nums) {
        if (nums.length == 0) {
            return result;
        }

        boolean[] used = new boolean[nums.length];
        List<Integer> path = new ArrayList<>(nums.length);
        dfs(nums, path, used);
        return result;
    }

    private void dfs(int[] nums,List<Integer> path, boolean[] used) {
        // 结束条件：数组遍历完成
        if (path.size() >= nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        // dfs：
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) {
                continue;
            }
            path.add(nums[i]);
            used[i] = true;
            dfs(nums, path, used);

            // 递归之后的回溯：表示当前下标为未使用，并删除使用的变量
            used[i] = false;
            path.remove(path.size() - 1);
        }
    }
}
