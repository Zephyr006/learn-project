package learn.leetcode;

import java.util.Arrays;

/**
 * 5. 最长回文子串
 *
 * 给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。
 *
 * 示例 1：
 *
 * 输入: "babad"
 * 输出: "bab"
 * 注意: "aba" 也是一个有效答案。
 * 示例 2：
 *
 * 输入: "cbbd"
 * 输出: "bb"
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-palindromic-substring
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2020/7/19.
 */
public class Medium03 {

    public static void main(String[] args) {
        new Medium03().longestPalindrome("babad");
    }

    public String longestPalindrome(String s) {
        if (s == null || s.length() <= 1)
            return s;

        // 每次判断是否是回文的子字符串长度
        int len = s.length();
        char[] chars = s.toCharArray();
        while (len > 1) {
            // i <= s.length() - len ： 子串能在完整字符串s中的偏移次数
            for (int i = 0; i <= s.length() - len; i++) {
                //String substring = s.substring(i, i + s.length() - len);
                char[] copyOfRange = Arrays.copyOfRange(chars, i, i + len);
                if (isPalindrome(copyOfRange))
                    return new String(copyOfRange);
            }
            len--;
        }
        return s.substring(0,1);
    }

    boolean isPalindrome(char[] chars) {
        for (int i = 0; i < chars.length / 2; i++) {
            if (chars[i] != chars[chars.length-i-1]) {
                return false;
            }
        }
        return true;
    }

    class Solution2 {
        /**
         * 中心回文:从某个位置开始向两端遍历判断子串是否回文,注意 子串回文有奇数和偶数两种情况
         */
        public String longestPalindrome(String s) {
            if (s.length() == 1)
                return s;
            if (s.length() == 2) {
                return s.charAt(0) == s.charAt(1) ? s : "" + s.charAt(0);
            }
            int left = 0, right = 0;
            for (int i = 0; i< s.length(); i++) {
                int len = longestPalindrome(s, i, i);
                // 总长度是奇数
                if (len > right - left + 1) {
                    left = i - len / 2;
                    right = i + len / 2;
                }
                if (i > 0 && i < s.length()) {
                    //  总长度是偶数
                    len = longestPalindrome(s, i - 1, i);
                    if (len > right - left + 1) {
                        left = i - len / 2;
                        right = i + len / 2 - 1;
                    }
                }
            }
            return s.substring(left, right + 1);
        }

        private int longestPalindrome(String s, int left, int right) {
            while (left >= 0 && right < s.length()) {
                if (s.charAt(left) == s.charAt(right)) {
                    left--;
                    right++;
                } else {
                    // 不满足回文条件时要跳出循环
                    break;
                }
            }
            // 注意此时的left和right位置的元素不满足回文条件，需要进行 -1 操作
            return right - left - 1;
        }
    }

    /*public String longestPalindrome(String s) {
        if (s == null || s.length() <= 1)
            return s;

        int len = 0;
        while (len < s.length()) {
            for (int i = 0; i <= len; i++) {
                String substring = s.substring(i, i + s.length() - len);
                if (isPalindrome(substring))
                    return substring;
            }
            len++;
        }
        return s.substring(0,1);
    }

    boolean isPalindrome(String s) {
        for (int i = 0; i < s.length() / 2; i++) {
            if (s.charAt(i) != s.charAt(s.length()-i-1)) {
                return false;
            }
        }
        return true;
    }*/
}
