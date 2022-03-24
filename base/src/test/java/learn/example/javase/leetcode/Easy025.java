package learn.example.javase.leetcode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 169. 多数元素
 *
 * 给定一个大小为 n 的数组，找到其中的多数元素。多数元素是指在数组中出现次数 大于 ⌊ n/2 ⌋ 的元素。
 *
 * 你可以假设数组是非空的，并且给定的数组总是存在多数元素。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：[3,2,3]
 * 输出：3
 * 示例 2：
 *
 * 输入：[2,2,1,1,1,2,2]
 * 输出：2
 *  
 *
 * 进阶：
 *
 * 尝试设计时间复杂度为 O(n)、空间复杂度为 O(1) 的算法解决此问题。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/majority-element
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2022/3/12.
 */
public class Easy025 {

    public static void main(String[] args) {
        new Easy025().majorityElementDivideAndConquer(new int[]{2,2,1,1,1,2,2});
    }

    /**
     * 暴力破解，将元素的出现次数记录在map中，最后通过遍历map找到出现次数最多的元素
     */
    public int majorityElement(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.compute(num, (k, count) -> {
                if (count == null) {
                    count = 0;
                }
                return count + 1;
            });
        }
        int th = nums.length / 2;
        for(int i : nums) {
            if (map.get(i) > th) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 优化思路（排序法）：先排序，那排好序之后的数组中，相同元素总是相邻的。
     * 即存在长度> ⌊ n/2 ⌋的一长串 由相同元素构成的连续子数组，数组中间的元素值总是“多数元素”，毕竟它 长度 > ⌊ n/2 ⌋。
     */
    public int majorityElementBySort(int[] nums) {
        Arrays.sort(nums);
        //注意这里的优化思路
        return nums[nums.length >> 1];
    }

    /**
     * 摩尔投票法思路:
     * 类似思路见 {@link Medium09}
     *
     * 候选人(cand_num)初始化为nums[0]，票数count初始化为1。
     * 当遇到与cand_num相同的数，则票数count = count + 1，否则票数count = count - 1。
     * 当票数count为0时，更换候选人，并将票数count重置为1。
     * 遍历完数组后，cand_num即为最终答案。
     *
     * 为何这行得通呢？
     * 投票法是遇到相同的则票数 + 1，遇到不同的则票数 - 1。
     * 且“多数元素”的个数> ⌊ n/2 ⌋，其余元素的个数总和<= ⌊ n/2 ⌋。
     * 因此“多数元素”的个数 - 其余元素的个数总和 的结果 肯定 >= 1。
     * 这就相当于每个“多数元素”和其他元素 两两相互抵消，**抵消到最后肯定还剩余至少1个“多数元素”**。
     *
     * 无论数组是1 2 1 2 1，亦或是1 2 2 1 1，总能得到正确的候选人。
     *
     * 作者：gfu
     * 链接：https://leetcode-cn.com/problems/majority-element/solution/3chong-fang-fa-by-gfu-2/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public int majorityElement11(int[] nums) {
        int cand_num = nums[0], count = 1;
        // 注意这里从下标1开始
        for (int i = 1; i < nums.length; ++i) {
            if (cand_num == nums[i])
                ++count;
            // 如果前面的元素都相互抵消了，则cand_num指向当前元素
            else if (--count == 0) {
                cand_num = nums[i];
                count = 1;
            }
        }
        return cand_num;
    }

    public int majorityElementDivideAndConquer(int[] nums) {
        return divideAndConquer(nums, 0, nums.length - 1);
    }

    /**
     * 分治法：理论基础是 求数组的众数
     *
     * 按照分治求解的三步走：
     *
     * 划分（Divide）
     * 求解（Conquer）
     * 合并（Combine）
     * 那整个问题的解决步骤就很明确了：
     *
     * (1) 划分
     *
     * 划分就是拆解到问题的最小规模，这里还是用到了二分的思想。
     *
     * 每次将数组拆分为左右两个区间，直至拆成最小规模的问题，每个区间只有一个数。
     *
     * (2) 求解
     *
     * 递归的求解划分之后的子问题。
     *
     * 在最小的区间里，每个区间只有一个数，那该区间的众数该数。
     *
     * (3) 合并
     *
     * 一步步的向上合并，合并过程中分为两种情况：
     *
     * 第一种：左右两个区间的众数相同，那直接返回这个众数。
     *
     * 第二种：左右两个区间的众数不同，这时就将两个区间合并，在合并后的区间种计算出这两个众数出现的次数，将数值大的众数返回。
     *
     * 这里面要注意一点的是，可能出现左右两个众数在合并后的区间中出现的次数相同，这种情况就先随便返回一个即可。
     *
     * 因为这只是过程中的产生情况，在合并的最终解不会出现这种情况。
     *
     * 毕竟【提示】中说了：数组非空，且给定的数组总是存在多数元素。
     *
     * 作者：rocky0429-2
     * 链接：https://leetcode-cn.com/problems/majority-element/solution/acm-xuan-shou-tu-jie-leetcode-169-duo-sh-y1sr/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     */
    public int divideAndConquer(int[] nums, int left, int right) {
        if (left >= right) {
            return nums[left];
        }
        if (right - left == 1) {
            return nums[left];
        }

        // 重点！！！！！ middle取值
        int middle = left + ((right - left) >> 1);
        int maxLeft = divideAndConquer(nums, left, middle);
        // 这里，右区间应该从middle+1开始
        int maxRight = divideAndConquer(nums, middle + 1, right);
        // 两个子任务返回的出现频率最大的元素值相同，返回任意一个
        if (maxLeft == maxRight) {
            return maxLeft;
        }

        return getMaxFreq(nums, left, right, maxLeft, maxRight);
    }

    private int getMaxFreq(int[] nums, int left, int right, int maxLeft, int maxRight) {
        int leftMaxCount = 0, rightMaxCount = 0;
        for (int i = left; i <= right; i++) {
            if (nums[i] == maxLeft) {
                leftMaxCount++;
            } else if (nums[i] == maxRight) {
                rightMaxCount++;
            }
        }
        return leftMaxCount > rightMaxCount ? maxLeft : maxRight;
    }


}
