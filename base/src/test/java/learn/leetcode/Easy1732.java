package learn.leetcode;

/**
 * 1732. 找到最高海拔
 *
 * https://leetcode.cn/problems/find-the-highest-altitude/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Easy1732 {

    public static void main(String[] args) {

    }

    /**
     * 前缀和
     */
    public int largestAltitude(int[] gain) {
        if (gain == null || gain.length == 0) {
            return 0;
        }
        int max = 0;
        int curr = 0;
        for (int i = 0; i < gain.length; i++) {
            curr += gain[i];
            max = Math.max(max, curr);
        }
        return max;
    }
}
