package learn.leetcode;

/**
 * 45. 跳跃游戏 II
 * https://leetcode.cn/problems/jump-game-ii/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium45 {
    public static void main(String[] args) {
        int jump = new Medium45().jump(new int[]{2, 3, 1, 2, 4, 2, 3});
        System.out.println(jump);
    }

    /**
     * 贪心算法：每次跳跃都尝试跳到可选范围内的最远下标位置，通过更新maxPosition来实现
     * 如果已经遍历到了当前能跳的最远位置，说明必须要跳一步了，此时的maxPosition就是下一步跳跃时能跳的最远位置（右边界下标）
     * https://leetcode.cn/problems/jump-game-ii/solutions/9347/xiang-xi-tong-su-de-si-lu-fen-xi-duo-jie-fa-by-10
     */
    public int jump(int[] nums) {
        //  currEnd 表示当前能跳的最远下标
        int currEnd = 0;
        int maxPosition = 0;
        int steps = 0;
        for (int i = 0; i < nums.length; i++) {
            // 更新 当前能跳到的最远下标位置
            maxPosition = Math.max(maxPosition, i + nums[i]);
            // 遇到边界（当前最远只能跳到这个下标位置），更新右边界为当前记录的最远位置，并记录跳了一次
            if (i == currEnd) {
                currEnd = maxPosition;
                steps++;
            }
        }
        return steps;
    }
}
