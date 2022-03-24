package learn.example.javase.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 229. 求众数 II
 *
 * 给定一个大小为 n 的整数数组，找出其中所有出现超过 ⌊ n/3 ⌋ 次的元素。
 *
 *  
 *
 *  
 *
 * 示例 1：
 *
 * 输入：[3,2,3]
 * 输出：[3]
 * 示例 2：
 *
 * 输入：nums = [1]
 * 输出：[1]
 * 示例 3：
 *
 * 输入：[1,1,1,3,3,2,2,2]
 * 输出：[1,2]
 *  
 *
 * 提示：
 *
 * 1 <= nums.length <= 5 * 104
 * -109 <= nums[i] <= 109
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/majority-element-ii
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2022/3/12.
 */
public class Medium09 {

    public static void main(String[] args) {
        List<Integer> integers = new Medium09().majorityElement(new int[]{1, 1, 1, 3, 3, 2, 2, 2});
    }

    /**
     * 摩尔投票法：注意题干说找出其中所有出现超过 ⌊ n/3 ⌋ 次的元素，可知出现次数超过 n/k 的数最多只有 k−1 个，但可能少于k-1个，所以需要k-1个参数，并且最终需要检查票数
     * 上述做法正确性的关键是：若存在出现次数超过 n / kn/k 的数，最后必然会成为这 k - 1k−1 个候选者之一
     *
     * 标准做法，在遍历数组时同时 check 这 k - 1k−1 个数，假设当前遍历到的元素为 xx：
     *
     * 如果 xx 本身是候选者的话，则对其出现次数加一；
     * 如果 xx 本身不是候选者，检查是否有候选者的出现次数为 00：
     * 若有，则让 xx 代替其成为候选者，并记录出现次数为 11；
     * 若无，则让所有候选者的出现次数减一。
     * 当处理完整个数组后，这 k - 1k−1 个数可能会被填满，但不一定都是符合出现次数超过 n / kn/k 要求的。
     *
     * 需要进行二次遍历，来确定候选者是否符合要求，将符合要求的数加到答案。
     *
     * 作者：AC_OIer
     * 链接：https://leetcode-cn.com/problems/majority-element-ii/solution/gong-shui-san-xie-noxiang-xin-ke-xue-xi-ws0rj/
     * 来源：力扣（LeetCode）
     * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
     *
     */
    public List<Integer> majorityElement(int[] nums) {
        // 创建返回值
        List<Integer> res = new ArrayList<>();
        if (nums == null || nums.length == 0)
            return res;
        // 初始化两个候选人candidate，和他们的计票，注意这里计数值从0开始，遍历也是从0位开始
        int cand1 = nums[0], count1 = 0;
        int cand2 = nums[0], count2 = 0;

        // 摩尔投票法，分为两个阶段：配对阶段和计数阶段
        // 配对阶段
        for (int num : nums) {
            // 投票
            if (cand1 == num) {
                count1++;
            }
            else if (cand2 == num) {
                count2++;
            }

            // 第1个候选人配对，谁先被抵消位0，谁换选举人（元素）
            else if (count1 == 0) {
                cand1 = num;
                count1++;
            }
            // 第2个候选人配对
            else if (count2 == 0) {
                cand2 = num;
                count2++;
            }
            // 当前元素num不是任何一个候选者，并且当前候选者的票数都大于0，则每个候选者的票数都 -1
            else {
                count1--;
                count2--;
            }
        }

        // 计数阶段
        // 找到了两个候选人之后，需要确定票数是否满足大于 N/3
        count1 = 0;
        count2 = 0;
        for (int num : nums) {
            if (cand1 == num) count1++;
            else if (cand2 == num) count2++;
        }

        if (count1 > nums.length / 3) res.add(cand1);
        if (count2 > nums.length / 3) res.add(cand2);

        return res;
    }


    public List<Integer> majorityElementFor4(int[] nums) {
        // 创建返回值
        List<Integer> res = new ArrayList<>();
        if (nums == null || nums.length == 0)
            return res;
        // 初始化两个候选人candidate，和他们的计票
        int cand1 = nums[0], count1 = 0;
        int cand2 = nums[0], count2 = 0;
        int cand3 = nums[0], count3 = 0;

        for (int n : nums) {
            //计数累加阶段
            if (cand1 == n) {
                count1++;
                continue;
            }
            if (cand2 == n) {
                count2++;
                continue;
            }
            if (cand3 == n) {
                count3++;
                continue;
            }

            // 候选人配对阶段
            if (count1 == 0) {
                cand1 = n;
                count1++;
                continue;
            }
            if (count2 == 0) {
                cand2 = n;
                count2++;
                continue;
            }
            if (count3 == 0) {
                cand3 = n;
                count3++;
                continue;
            }
            count1--;
            count2--;
            count3--;
        }

        count1 = count2 = count3 = 0;
        for (int n : nums) {
            if (n == cand1) {
                count1++;
            }
            // 用else if的形式，如果三个元素中有相等的值，可以避免重复累加，最终只会有一个元素被加入到结果集
            else if (n == cand2) {
                count2++;
            }
            else if (n == cand3) {
                count3++;
            }
        }
        if (count1 > nums.length / 4)
            res.add(cand1);
        if (count2 > nums.length / 4)
            res.add(cand2);
        if (count3 > nums.length / 4)
            res.add(cand3);
        return res;
    }
}
