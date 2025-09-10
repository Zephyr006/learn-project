package learn.leetcode;

/**
 * 给你一个字符串 s ，请你反转字符串中 单词 的顺序。
 *
 * 单词 是由非空格字符组成的字符串。s 中使用至少一个空格将字符串中的 单词 分隔开。
 *
 * 返回 单词 顺序颠倒且 单词 之间用单个空格连接的结果字符串。
 *
 * 注意：输入字符串 s中可能会存在前导空格、尾随空格或者单词间的多个空格。返回的结果字符串中，单词间应当仅用单个空格分隔，且不包含任何额外的空格。
 *
 * 示例 1：
 *
 * 输入：s = "the sky is blue"
 * 输出："blue is sky the"
 * 示例 2：
 *
 * 输入：s = "  hello world  "
 * 输出："world hello"
 * 解释：反转后的字符串中不能存在前导空格和尾随空格。
 * 示例 3：
 *
 * 输入：s = "a good   example"
 * 输出："example good a"
 * 解释：如果两个单词间有多余的空格，反转后的字符串需要将单词间的空格减少到仅有一个。
 * 提示：
 *
 * 1 <= s.length <= 104
 * s 包含英文大小写字母、数字和空格 ' '
 * s 中 至少存在一个 单词
 * 进阶：如果字符串在你使用的编程语言中是一种可变数据类型，请尝试使用 O(1) 额外空间复杂度的 原地 解法。
 *
 * https://leetcode.cn/problems/reverse-words-in-a-string/description/
 */
public class Medium151 {

    public static void main(String[] args) {
        System.out.println(new Medium151().reverseWords("  hello world  "));
        System.out.println(new Medium151().reverseWords("the sky is blue"));
    }

    public String reverseWordsDoublePoints(String s) {
        /**
         * 双指针解法
         */
        s = s.trim();
        StringBuilder builder = new StringBuilder();
        int left = s.length() - 1, right = left;
        while (left >= 0) {
            while (left >= 0 && s.charAt(left) != ' ') {
                left--;
            }
            // 找到第一个空格
            builder.append(s.substring(left + 1, right + 1)).append(' ');
            //  跳过连续的空额
            while (left >= 0 && s.charAt(left) == ' ') {
                left--;
            }
            right = left;
        }
        // 去掉多余的空格
        return builder.toString().trim();
    }

    /**
     * 从后向前遍历的解法
     */
    public String reverseWords(String s) {
        int idx = s.length() - 1;
        StringBuilder builder = new StringBuilder();
        while (true) {
            // 每次遍历都要重新清空临时数据
            String oneWord = null;
            Integer endIdx = null;
            // 从后向前，遍历找到每一个单子
            while (idx >= 0) {
                if (endIdx == null && s.charAt(idx) != ' ') {
                    endIdx = idx;
                }
                if (endIdx != null && s.charAt(idx) == ' ') {
                    oneWord = s.substring(idx + 1, endIdx + 1); //endIndex不包含最后一个 index，需要 endIndex+1
                    break;
                }
                // 第一个字母非空格的情况
                if (idx == 0 && s.charAt(idx) != ' ') {
                    oneWord = s.substring(idx, endIdx + 1);
                }
                idx--;
            }

            // 如果找到的单词为空，说明已经遍历完了，否则保存这个单词
            if (oneWord == null) {
                return builder.substring(1);
            } else {
                builder.append(' ').append(oneWord);
            }
        }
    }

}
