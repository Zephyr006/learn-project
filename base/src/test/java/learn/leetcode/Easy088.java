package learn.leetcode;

/**
 * 88. 合并两个有序数组
 *
 * 给你两个有序整数数组 nums1 和 nums2，请你将 nums2 合并到 nums1 中，使 nums1 成为一个有序数组。
 *
 * 说明:
 * 初始化 nums1 和 nums2 的元素数量分别为 m 和 n 。
 * 你可以假设 nums1 有足够的空间（空间大小大于或等于 m + n）来保存 nums2 中的元素。
 *
 * 示例:
 * 输入:
 * nums1 = [1,2,3,0,0,0], m = 3
 * nums2 = [2,5,6],       n = 3
 *
 * 输出: [1,2,2,3,5,6]
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/merge-sorted-array
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2020/7/14.
 */
public class Easy088 {

    public static void main(String[] args) {
        int[] a = new int[]{1,2,3,0,0,0};
        int[] b = new int[]{2,5,6};
        new Easy088().merge(a, 3, b, 3);
    }

    public void merge(int[] nums1, int m, int[] nums2, int n) {
        if (m <= 0 && n > 0) {
            System.arraycopy(nums2, 0, nums1, 0, n);
            return;
        }
        if (m > 0 && n > 0) {
            int p = 0;
            for (int i = 0; i < m+n; i++) {
                // 如果nums1中的当前元素较大，则当前位置的元素及后续元素向后移动一位
                if (nums1[i] > nums2[p]) {
                    System.arraycopy(nums1, i, nums1, i+1, m-i+p);
                    nums1[i] = nums2[p];
                    p++;
                }
                //当nums2数组的所有元素都处理完成时，要退出循环，否则在 `if (nums1[i] > nums2[p])` 处会IndexOutOfBoundsException
                if (p >= n)
                    return;
            }
            //如果一个循环结束，nums2数组中扔有元素未被处理，则统一都复制过去
            if (p < n) {
                System.arraycopy(nums2, p, nums1, m+p, n-p);
            }
        }
    }

    /**
     * 官方思路：
     * 1、复制nums1中的元素到nums1_copy
     * 2、同时遍历 nums1_copy 和 nums2 ，按大小顺序依次放入nums1
     */
    public void mergeOfficial(int[] nums1, int m, int[] nums2, int n) {
        // Make a copy of nums1.
        int [] nums1_copy = new int[m];
        System.arraycopy(nums1, 0, nums1_copy, 0, m);

        // Two get pointers for nums1_copy and nums2.
        int p1 = 0;
        int p2 = 0;

        // Set pointer for nums1
        int p = 0;

        // Compare elements from nums1_copy and nums2
        // and add the smallest one into nums1.
        while ((p1 < m) && (p2 < n))
            nums1[p++] = (nums1_copy[p1] < nums2[p2]) ? nums1_copy[p1++] : nums2[p2++];

        // if there are still elements to add
        if (p1 < m)
            System.arraycopy(nums1_copy, p1, nums1, p1 + p2, m + n - p1 - p2);
        if (p2 < n)
            System.arraycopy(nums2, p2, nums1, p1 + p2, m + n - p1 - p2);
    }
}
