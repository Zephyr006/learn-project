package learn.leetcode;

import java.util.LinkedList;

/**
 * 739. 每日温度
 * https://leetcode.cn/problems/daily-temperatures/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium739 {

    /**
     * 单调递减栈：遍历数组，将遍历过程中遇到的小于等于栈顶元素值入栈，
     * 具体步骤：
     * 遍历整个数组，如果栈不空，且当前数字大于栈顶元素，那么如果直接入栈的话就不是 递减栈 ，所以需要取出栈顶元素，
     * 由于当前数字大于栈顶元素的数字，而且一定是第一个大于栈顶元素的数，直接求出下标差就是二者的距离。
     *
     * 继续看新的栈顶元素，直到当前数字小于等于栈顶元素停止，然后将数字入栈，这样就可以一直保持递减栈，且每个数字和第一个大于它的数的距离也可以算出来。
     */
    public int[] dailyTemperatures(int[] temperatures) {
        int len = temperatures.length;
        int[] result = new int[len];
        LinkedList<Integer> list = new LinkedList<>();

        for (int i = 0; i < len; i++) {
            // 如果栈不为空，并且当前元素大于栈顶元素，说明遇到了第一个满足题意（出现更高温度/更大值）的结果，记录结果值到 result
            while (list.size() > 0 && temperatures[i] > temperatures[list.getLast()]) {
                int pre = list.removeLast();
                result[pre] = i - pre;
            }
            // 将当前下标入栈
            list.addLast(i);
        }
        return result;
    }
}
