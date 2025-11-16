package learn.leetcode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 216. 组合总和 III
 */
public class Medium216 {

    public static void main(String[] args) {
        new Medium216.Solution().combinationSum3(3, 7);
    }

    private static class Solution {
        List<List<Integer>> result = new ArrayList<>();
        public List<List<Integer>> combinationSum3(int k, int n) {
            List<Integer> used = new ArrayList<>(k); // 用List存路径，更直观（无需HashSet）
            dfs(k, n, 0, 1, used);
            return result;
        }
        /**
         * @param start 循环从start开始，而非1
         */
        private void dfs(int k, int n, int sum, int start, List<Integer> used) {
            if (sum == n && used.size() == k) {
                result.add(new ArrayList<>(used));
                return;
            }
            if (used.size() >= k) {
                return;
            }

            // dfs
            for (int i = start; i <= 9; i++) {
                // 剪枝
                if (sum + i > n) {
                    break;
                }
                // 剪枝2：剩余数字个数不足（k - path.size()），直接break（优化效率）
                if (9 - i + 1 < k - used.size()) {
                    break;
                }
                used.add(i);
                dfs(k, n, sum + i, i + 1, used);

                // 回溯：删掉用过的元素值 i
                used.remove(used.size() - 1);
            }
        }
    }

}
