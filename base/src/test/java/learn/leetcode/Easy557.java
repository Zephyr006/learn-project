package learn.leetcode;

/**
 * 557. 反转字符串中的单词 III
 * { @link https://leetcode.cn/problems/reverse-words-in-a-string-iii/}
 *
 * @author Zephyr
 * @date 2023/5/27.
 */
public class Easy557 {

    public static void main(String[] args) {
        System.out.println(new Solution().reverseWords("hehhhhhhe"));
    }

    static class Solution {
        public String reverseWords(String s) {
            if (s.length() == 0)
                return s;
            char[] chars = s.toCharArray();
            int slow = 0;
            int fast = 1;
            while (fast < s.length()) {
                // "_s"，fast当前指向一个单词的最后一个字母
                if (chars[fast - 1] == ' ' && chars[fast] != ' ') {
                    slow = fast;
                }
                // "s_", fast当前指向空格
                if (chars[fast - 1] != ' ' && chars[fast] == ' ') {
                    reverse(chars, slow, fast - 1);
                    // s 中的所有单词都用一个空格隔开。
                    slow = fast + 1;
                }
                fast++;
            }
            // 注意:处理字符串的最后一个字母
            if (slow < fast) {
                reverse(chars, slow, fast - 1);
            }

            return new String(chars);
        }

        /**
         * 调换两个位置的字母
         */
        private void reverse(char[] chars, int left, int right) {
            char tmp;
            while (left < right) {
                tmp = chars[left];
                chars[left] = chars[right];
                chars[right] = tmp;
                left++;
                right--;
            }
        }
    }
}
