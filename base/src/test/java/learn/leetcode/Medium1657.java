package learn.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 1657. 确定两个字符串是否接近
 *
 * https://leetcode.cn/problems/determine-if-two-strings-are-close/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium1657 {
    public static void main(String[] args) {
        // LeetcodeHelper.invokePublicMethods("cabbba", "aabbss");
        // LeetcodeHelper.invokePublicMethods("abc", "cba");
        LeetcodeHelper.invokePublicMethods("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii", "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
    }

    /**
     * 哈希：转换一下题意，如果要满足题目要求，需要
     * 1. 两个字符串的长度相等
     * 2. 两个字符串中出现的字符次数必须完全一致，次数对应的字符可以不一致  ，即 分别将两个字符串的字符出现次数数组 f1 和 f2进行排序后，两个数组从小到大一一相等。
     */
    public boolean closeStrings(String word1, String word2) {
        if ( word1.length() != word2.length() ){
            return false;
        }

        // 先记录每个字符出现的次数
        Map<Character, Integer> map1 = new HashMap<>();
        Map<Character, Integer> map2 = new HashMap<>();
        for (int i = 0; i < word1.length(); i++) {
            map1.compute(word1.charAt(i), (k, v) -> v == null ? 1 : v + 1);
        }
        for (int i = 0; i < word2.length(); i++) {
            map2.compute(word2.charAt(i), (k, v) -> v == null ? 1 : v + 1);
        }

        // 必须是现有字符，所以每个字符都需要包含在另一个字符串中
        if (map1.size() != map2.size() || !map1.keySet().containsAll(map2.keySet())){
            return false;
        }

        // 判断出现次数：出现次数按从小到大排序后，需要满足出现的次序完全一致
        ArrayList<Integer> times1 = new ArrayList<>(map1.values());
        ArrayList<Integer> times2 = new ArrayList<>(map2.values());
        times1.sort(Integer::compareTo);
        times2.sort(Integer::compareTo);

        for (int i = 0; i < times1.size(); i++) {
            if (!Objects.equals(times1.get(i), times2.get(i))){
                return false;
            }
        }
        return true;
    }
}
