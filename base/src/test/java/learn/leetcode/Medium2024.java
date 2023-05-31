package learn.leetcode;

/**
 * 2024. 考试的最大困扰度
 *
 * @author Zephyr
 * @date 2022/4/5.
 */
public class Medium2024 {
    public static void main(String[] args) {

    }

    class Solution {
        /**
         * 题目求修改次数不超过 kk 的前提下，连续段 'T' 或 'F' 的最大长度。
         * 等价于求一个包含 'F' 或者 'T' 的个数不超过 kk 的最大长度窗口。
         *
         * 滑动窗口法
         */
        public int maxConsecutiveAnswers(String answerKey, int k) {
            int maxLen = 0, tCount = 0, fCount = 0;
            char[] chars = answerKey.toCharArray();
            int left = 0, right = 0;
            for (; right < chars.length; right++) {
                // 对当前字符累加计数
                if (chars[right] == 'T') {
                    tCount++;
                } else {
                    fCount++;
                }
                // 如果当前元素加入到窗口后，无法满足不超过k次的最大连续，就需要删除一些元素
                if (tCount > k && fCount > k) {
                    // 记录下当前满足条件的最大长度（长度不包含right指针对应的元素）
                    maxLen = Math.max(maxLen, right - left);
                    // 从最左侧开始删除字符，直到 [left，right] 区间恢复合法性
                    do
                    {
                        char leftChar = chars[left++];
                        if (leftChar == 'T') {
                            tCount--;
                        } else {
                            fCount--;
                        }
                    } while (tCount > k && fCount > k);
                }
            }
            // 注意统计最后一个窗口的长度
            return Math.max(maxLen, right - left);
        }
    }
}
