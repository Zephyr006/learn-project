package learn.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * 3. 无重复字符的最长子串
 * https://leetcode.cn/problems/wtcaE1/description/
 */
public class Medium03 {
    public static void main(String[] args) {

    }

    /**
     * 滑动窗口：如果变量的是没有出现过的字符，则right指针始终向右，如果出现了重复字符，则 left 窗口右移，直至重复字符从右侧移除
     */
    public int lengthOfLongestSubstring(String s) {
        int max = 0;
        int left = 0, right;
        Set<Character> set = new HashSet<>();
        for (right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (!set.contains(c)) {
                set.add(c);
            } else {
                max = Math.max(max, set.size());
                while (s.charAt(left) != c) {
                    set.remove(s.charAt(left));
                    left++;
                }
                left++;
            }
        }
        // 可能遍历到最后都没有出现重复字符，则需要判断下最大长度
        return Math.max(max, set.size());
    }
}
