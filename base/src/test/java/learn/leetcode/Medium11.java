package learn.leetcode;

/**
 * 11. 盛最多水的容器
 *
 * https://leetcode.cn/problems/container-with-most-water/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium11 {

    public static void main(String[] args) {

    }

    /**
     * 双指针解法：左右两个指针，每次都移动对应值相对较小的那个指针
     */
    public int maxArea(int[] height) {
        int left = 0, right = height.length - 1, max = 0;
        while (left < right) {
            int h = Math.min(height[left], height[right]);
            max = Math.max(h * (right - left), max);
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return max;
    }

}
