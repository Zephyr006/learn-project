package learn.leetcode;

/**
 * 55. 跳跃游戏
 * https://leetcode.cn/problems/jump-game/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium55 {
    public static void main(String[] args) {
        boolean b = new Medium55().canJump(new int[]{2, 3, 1, 1, 4});
        System.out.println(b);
    }

    public boolean canJump(int[] nums) {
        return dfs(nums, 0);
    }

    private boolean dfs(int[] nums, int start) {
        // 结束条件：遍历到数组的结尾
        if (start == nums.length) {
            return true;
        }

        // dfs
        int t = Math.min(nums[start], nums.length - start);
        for (int i = t; i > 0; i--) {
            if (dfs(nums, start + i)) {
                return true;
            }
        }
        return false;
    }
}
