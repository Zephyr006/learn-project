package learn.leetcode;

/**
 * 75. 颜色分类
 * https://leetcode.cn/problems/sort-colors/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium75 {

    /**
     * 双指针：用指针 p0 来交换 0，p1来交换 1，初始值都为 0。当我们从左向右遍历整个数组时：
     */
    public void sortColors(int[] nums) {
        int p0 = 0, p1 = 0;
        for (int i = 0; i < nums.length; i++) {
            //
            if (nums[i] == 0) {
                int temp = nums[p0];
                nums[p0] = nums[i];
                nums[i] = temp;
                // 关键逻辑
                // 因为连续的 0 之后是连续的 1，因此如果我们将 0 与 nums[p0] 进行交换，那么我们可能会把一个 1 交换出去。
                // 因此，如果 p0 < p1，那么我们需要再将 nums[i] 与 nums[p1] 进行交换，
                // 其中 i 是当前遍历到的位置，在进行了第一次交换后，nums[i] 的值为 1，
                // 我们需要将这个 1 放到「头部」的末端。
                if (p0 < p1) {
                    int temp2 = nums[p1];
                    nums[p1] = nums[i];
                    nums[i] = temp2;
                }
                p0++;
                p1++;
            } else if (nums[i] == 1) {
                int temp = nums[i];
                nums[i] = nums[p1];
                nums[p1] = temp;
                ++p1;
            }
        }
    }
}
