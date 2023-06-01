package learn.leetcode;

/**
 * 796. 旋转字符串
 *
 * @author Zephyr
 * @date 2022/4/7.
 */
public class Easy796 {

    static class Solution {
        public boolean rotateString(String s, String goal) {
            return s.length() == goal.length() && (s + s).contains(goal);
        }
    }
}
