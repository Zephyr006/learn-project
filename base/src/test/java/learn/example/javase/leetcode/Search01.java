package learn.example.javase.leetcode;

/**
 * @author Zephyr
 * @date 2022/3/13.
 */
public class Search01 {

    public static void main(String[] args) {
        int search = new Search01().search2(new int[]{-1, 0, 3, 5, 9, 12}, 9);
        System.out.println(search);
    }

    /**
     * 标准的二分查找法找到，找到目标元素后返回元素下标
     */
    public int search(int[] nums, int target) {
        int left = 0, right = nums.length - 1, mid = right / 2;
        while (left < right) {
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                //注意这里，不能只是=middle，当left=middle时无法正常结束循环
                left = mid + 1;
                mid = left + (right - left) / 2;
            } else {
                right = mid - 1;
                mid = left + (right - left) / 2;
            }
        }
        return left == right && nums[left] == target ? left : -1;
    }

    public int search2(int[] nums, int target) {
        return searchByRecursion(nums, target, 0, nums.length - 1);
    }

    /**
     * 递归解法
     */
    public int searchByRecursion(int[] nums, int target, int left, int right) {
        if (right - left <= 0) {
            return nums[left] == target ? left : -1;
        }

        // 注意middle的取值！！！！！！！！！
        int mid = left + (right - left) / 2;
        if (nums[mid] == target) {
            return mid;
        }
        if (nums[mid] < target) {
            return searchByRecursion(nums, target, mid + 1, right);
        } else {
            return searchByRecursion(nums, target, left, mid - 1);
        }
    }
}
