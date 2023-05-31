package learn.leetcode;

/**
 * @author Zephyr
 * @date 2022/4/27.
 */
public class Offer58 {

    public static void main(String[] args) {
        String s = "a good   example";
        String s1 = new Solution().reverseWords(s);
        System.out.println(s1);
    }

    static class Solution {
        public String reverseWords(String s) {
            if (s == null) { return s; }

            char[] res = new char[s.length()];
            int subSize = 0, total = 0;
            char[] chars = s.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] != ' ') {
                    subSize++;

                // 处理完成了一个单词
                } else if (subSize > 0) {
                    System.arraycopy(chars, i - subSize, res, res.length - total - subSize, subSize);
                    res[res.length - 1 - total - subSize] = ' ';
                    total += subSize + 1;
                    subSize = 0;
                } else {
                    // 当前是空格，并且是多余空格
                }
            }
            if (subSize > 0) {
                System.arraycopy(chars, chars.length - subSize, res, res.length - total - subSize, subSize);
                total += subSize;
            } else {
                total = total > 0 ? total - 1 : total;
            }

            return new String(res, res.length - total, total);
        }


    }
}
