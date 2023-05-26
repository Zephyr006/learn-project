package learn.leetcode;

/**
 * 344. 反转字符串
 * { @link https://leetcode.cn/problems/reverse-string/solution/}
 *
 * @author Zephyr
 * @since 2023-5-22.
 */
public class Medium15 {

    public static void main(String[] args) {
        System.out.println(new Solution().reverseWords("the sky is blue"));
    }

    static class Solution {
        public String reverseWords(String s) {
            // 逆序遍历字符串，
            StringBuilder sb = new StringBuilder();
            int left = s.length() - 1, right = left;
            for (int i = left; i >= 0; i--) {
                if (s.charAt(i) == ' ') {
                    if(left < right) {
                        sb.append(' ').append(s, left + 1, right + 1);
                        right = i - 1;
                        left = right;
                    } else if (left == right) {
                        left--;
                        right--;
                    }
                } else {
                    left--;
                }
            }
            if (left < right) {
                sb.append(' ').append(s.substring(left + 1, right + 1));
            }
            return sb.substring(1, sb.length());
        }
    }
}
