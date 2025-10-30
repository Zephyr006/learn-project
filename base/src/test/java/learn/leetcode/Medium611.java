package learn.leetcode;

import java.util.Arrays;

/**
 * 611. 有效三角形的个数
 * https://leetcode.cn/problems/valid-triangle-number/description/
 */
public class Medium611 {
    public static void main(String[] args) {
        int i = new Medium611().triangleNumber(new int[]{7, 0, 0, 0});
        System.out.println(i);
    }

    /**
     * 排序+二分查找：三角形由三条边组成，固定左边界的两条边，找到能构成三角形的第三条边位置 k，j 和 k 中间的数都能构成三角形
     */
    public int triangleNumber2(int[] nums) {
        Arrays.sort(nums);
        int result = 0;
        for (int i = 0; i < nums.length - 2; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                int left = j + 1, right = nums.length - 1;
                // k表示三角形中第三条边的下标位置，初始值为 j 表示如果没有找到满足三角形条件的第三条边，那么 k-j = 0，在执行结果 result 累加时能保证最终结果 result 是正确的
                int k = j;
                while (left <= right) {
                    int mid = (left + right) / 2;
                    if (nums[i] + nums[j] > nums[mid]) {
                        // 注意：这里的 mid 位置满足题意，需要更新边界位置，这里没有使用 left 或者 right，而是使用 k 来记录
                        k = mid;
                        left = mid + 1;
                    } else {
                        right = mid - 1;
                    }
                }
                result += k - j;
            }
        }
        return result;
    }

    /**
     * 排序+双指针：固定第一个指针 i，把另外两条边的对应指针 j、k 理解成同向 向右的指针
     */
    public int triangleNumber(int[] nums) {
        Arrays.sort(nums);
        int result = 0;
        int k;
        for (int i = 0; i < nums.length - 2; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                k = j;
                // k 指针右移，直到遍历到数组边界为止或者遇到不满足三角形条件
                while (k < nums.length - 1 && nums[i] + nums[j] > nums[k + 1] ) {
                    k++;
                }
                result += k - j;
            }
        }
        return result;
    }

}
