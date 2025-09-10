package learn.leetcode;

/**
 * 给你两个字符串 word1 和 word2 。请你从 word1 开始，通过交替添加字母来合并字符串。如果一个字符串比另一个字符串长，就将多出来的字母追加到合并后字符串的末尾。
 *
 * 返回 合并后的字符串 。
 * https://leetcode.cn/problems/merge-strings-alternately/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Easy1768 {

    public static void main(String[] args) {
        String result = new Easy1768().mergeAlternately("abc", "pqr");
        System.out.println(result);
    }

    public String mergeAlternately(String word1, String word2) {
        int len1 = word1.length();
        int len2 = word2.length();
        int len = Math.max(len1, len2);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (i < len1) {
                sb.append(word1.charAt(i));
            }
            if (i < len2) {
                sb.append(word2.charAt(i));
            }
        }
        return sb.toString();
    }
}
