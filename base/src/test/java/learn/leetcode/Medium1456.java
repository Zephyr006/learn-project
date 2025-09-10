package learn.leetcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 1456. 定长子串中元音的最大数目
 *
 * 给你字符串 s 和整数 k 。
 *
 * 请返回字符串 s 中长度为 k 的单个子字符串中可能包含的最大元音字母数。
 *
 * 英文中的 元音字母 为（a, e, i, o, u）。
 *
 * https://leetcode.cn/problems/maximum-number-of-vowels-in-a-substring-of-given-length/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium1456 {

    public static void main(String[] args) {
        LeetcodeHelper.invokePublicMethods("abciiidef", 3);
    }

    Set<Character> yuanyin = new HashSet<>(Arrays.asList('a','e','i','o','u'));
    public int maxVowels(String s, int k) {
        int count = 0;
        int max = 0;
        for (int i = 0; i < s.length(); i++) {
            // not enough for k, just count
            count += yuanyin.contains(s.charAt(i)) ? 1 : 0;
            if (i < k) {
                max = Math.max(max, count);
                continue;
            } else {
                // 滑动窗口中已经够 k 个元素了，每次再右移的时候都需要移除窗口左边的元音计数
                count += yuanyin.contains(s.charAt(i - k)) ? -1 : 0;
                max = Math.max(max, count);
            }
        }
        return max;
    }

}
