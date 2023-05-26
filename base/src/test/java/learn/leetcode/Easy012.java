package learn.leetcode;

/**
 * 35. 搜索插入位置
 *
 * 给定一个排序数组和一个目标值，在数组中找到目标值，并返回其索引。如果目标值不存在于数组中，返回它将会被按顺序插入的位置。
 *
 * 你可以假设数组中无重复元素。
 *
 * 示例 1:
 *
 * 输入: [1,3,5,6], 5
 * 输出: 2
 * 示例 2:
 *
 * 输入: [1,3,5,6], 2
 * 输出: 1
 * 示例 3:
 *
 * 输入: [1,3,5,6], 7
 * 输出: 4
 * 示例 4:
 *
 * 输入: [1,3,5,6], 0
 * 输出: 0
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/search-insert-position
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @since 2020-07-06.
 */
public class Easy012 {

    /**
     * 折半查找的思想
     * @param nums
     * @param target
     * @return
     */
    public int searchInsert(int[] nums, int target) {
        if (nums == null || nums.length < 1)
            return 0;

        int right = nums.length -1;  //注意：right = nums.length -1
        int left = 0;
        while (left <= right) {      //注意：left <= right
            int middle = (right + left) >> 1;
            if (target < nums[middle]) {
                right = middle - 1;  //注意：right = middle - 1
            }
            else if (target > nums[middle]) {
                left = middle + 1;   //注意：left = middle + 1
            }
            else {
                return middle;
            }
        }
        return left;                // return left
    }

    public static void main(String[] args) {
        System.out.println(new Easy012().searchInsert(new int[]{1,3}, 2));;
    }

    private int searchInsert2(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        if (nums.length == 1) {
            return nums[0] == target ? 0 : (nums[0] > target ? 0 : 1);
        }

        int len = nums.length;
        int left = 0, right = len >> 1;
        int rightNum = nums[right];
        // 注意停止循环的条件
        while (left < right) {
            // 如果右侧指针值等于目标值，直接返回结果'right'
            if (rightNum == target) {
                return right;
            // 如果右侧指针值小于目标值，说明目标值在right指针的右侧，取当前right指针与数组结尾的'折半'位置
            } else if (rightNum < target) {
                left = right;
                right = (left + len) >> 1;
            // right指针值大于target值，取left指针与right指针的'折半'
            } else {
                right = (left + right) >> 1;
            }
        }
        // 退出循环后，当前right指针所在位置可能是target值的左侧一个位置，要考虑是否需要右移一位
        return rightNum < target ? right + 1 : right;
    }
}
