package learn.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * 438. 找到字符串中所有字母异位词
 * https://leetcode.cn/problems/find-all-anagrams-in-a-string/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium438 {
    public static void main(String[] args) {
        List<Integer> res = new Medium438().findAnagrams("cbaebabacd", "abc");
        System.out.println(res);
    }

    /**
     * 滑动窗口：
     */
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> list = new ArrayList<>();
        if (s.length() < p.length()) {
            return list;
        }
        // 因为都是小写字母，用数组记录字符串 p 中出现的字符次数
        int[] token = new int[26];
        for (char c : p.toCharArray()) {
            token[c - 'a']++;
        }

        // 滑动窗口：right 指针右移，每移动一次，就判断是否满足题意，并记录答案；同时根据需要移动 left 指针
        for (int left = 0, right = 0; right < s.length(); right++) {
            token[s.charAt(right) - 'a']--;

            if (right - left + 1 < p.length()) {
            } else if (right - left + 1 == p.length()) {
                if (isValid(token)) {
                    list.add(left);
                }
            } else {
                token[s.charAt(left) - 'a']++;
                left++;
                if (isValid(token)) {
                    list.add(left);
                }
            }

        }
        return list;
    }

    private boolean isValid(int[] token) {
        for (int n : token) {
            if (n != 0) {
                return false;
            }
        }
        return true;
    }
}
