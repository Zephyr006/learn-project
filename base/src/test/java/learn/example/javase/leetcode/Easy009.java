package learn.example.javase.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * 26. 删除排序数组中的重复项 （与 Easy10 类似）
 *
 * 给定一个排序数组，你需要在 原地 删除重复出现的元素，使得每个元素只出现一次，返回移除后数组的新长度。
 *
 * 不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成。
 *
 *  
 *
 * 示例 1:
 *
 * 给定数组 nums = [1,1,2],
 *
 * 函数应该返回新的长度 2, 并且原数组 nums 的前两个元素被修改为 1, 2。
 *
 * 你不需要考虑数组中超出新长度后面的元素。
 * 示例 2:
 *
 * 给定 nums = [0,0,1,1,1,2,2,3,3,4],
 *
 * 函数应该返回新的长度 5, 并且原数组 nums 的前五个元素被修改为 0, 1, 2, 3, 4。
 *
 * 你不需要考虑数组中超出新长度后面的元素。
 *  
 *
 * 说明:
 *
 * 为什么返回数值是整数，但输出的答案是数组呢?
 *
 * 请注意，输入数组是以「引用」方式传递的，这意味着在函数里修改输入数组对于调用者是可见的。
 *
 * 你可以想象内部操作如下:
 *
 * // nums 是以“引用”方式传递的。也就是说，不对实参做任何拷贝
 * int len = removeDuplicates(nums);
 *
 * // 在函数里修改输入数组对于调用者是可见的。
 * // 根据你的函数返回的长度, 它会打印出数组中该长度范围内的所有元素。
 * for (int i = 0; i < len; i++) {
 *     print(nums[i]);
 * }
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/remove-duplicates-from-sorted-array
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2020/7/4.
 */
public class Easy009 {

    public static void main(String[] args) {
        new Easy009().removeDuplicates(new int[]{0,0,1,1,1,2,2,3,3,4});
    }

    /**
     * 自己写的，思路：将前面重复的元素移动到数组后面
     * 优化的思路：由于数组是有序的，所以可以把不重复的数据移动到前面
     */
    public int removeDuplicates(int[] nums) {
        if (nums.length <= 1)
            return nums.length;

        int duplicateCount = 0;
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < nums.length - duplicateCount;) {
            if (set.contains(nums[i])) {
                // swap
                int temp = nums[i];
                for (int j = i; j < nums.length - duplicateCount -1; j++) {
                    if (nums[j] != nums[j+1])
                    nums[j] = nums[j+1];
                }

                //nums[i] = nums[nums.length - duplicateCount -1];
                nums[nums.length - duplicateCount -1] = temp;
                duplicateCount++;
            } else {
                set.add(nums[i]);
                // 只有当前位置的元素不与之前的元素重复时，才判断下一个元素，
                // 如果与之前的元素重复，则元素经过位置变化，当前位置的值已经变了
                i++;
            }
        }

        return set.size();
    }

    /**
     * 官方方法：双指针法
     *
     * 数组完成排序后，我们可以放置两个指针 i 和 j，其中 i 是慢指针，而 j 是快指针。只要 nums[i] = nums[j]，我们就增加 j 以跳过重复项。
     *
     * 当我们遇到 nums[j] ！= nums[i]nums[j] 时，跳过重复项的运行已经结束，
     * 因此我们必须把它（nums[j]）的值复制到 nums[i + 1]。
     * 然后递增 i，接着我们将再次重复相同的过程，直到 j 到达数组的末尾为止。
     *
     */
    public int removeDuplicatesOfficial(int[] nums) {
        if (nums.length == 0) return 0;
        int i = 0;
        for (int j = 1; j < nums.length; j++) {
            if (nums[j] != nums[i]) {
                i++;
                nums[i] = nums[j];
            }
        }
        return i + 1;
    }

}
