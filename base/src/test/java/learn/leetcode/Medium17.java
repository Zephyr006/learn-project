package learn.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 17. 电话号码的字母组合
 * https://leetcode.cn/problems/letter-combinations-of-a-phone-number/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium17 {
    public static void main(String[] args) {
        List<String> strings = new Medium17().letterCombinations("23");
        System.out.println(strings);
    }

    List<String> result = new ArrayList<>();
    public List<String> letterCombinations(String digits) {
        if (digits == null || digits.isEmpty()) {
            return result;
        }
        Map<Character, List<Character>> map = new HashMap<>();
        map.put('2', Arrays.asList('a', 'b', 'c'));
        map.put('3', Arrays.asList('d', 'e', 'f'));
        map.put('4', Arrays.asList('g', 'h', 'i'));
        map.put('5', Arrays.asList('j', 'k', 'l'));
        map.put('6', Arrays.asList('m', 'n', 'o'));
        map.put('7', Arrays.asList('p', 'q', 'r', 's'));
        map.put('8', Arrays.asList('t', 'u', 'v'));
        map.put('9', Arrays.asList('w', 'x', 'y', 'z'));

        dfs(map, digits, 0, new StringBuilder());
        return result;
    }

    private void dfs(Map<Character, List<Character>> map, String digits, int index, StringBuilder builder) {
        // 结束条件：digits遍历到了结尾
        if (index == digits.length()) {
            result.add(builder.toString());
            return;
        }

        // dfs：针对当前下标对应的数字进行追加操作
        List<Character> characters = map.get(digits.charAt(index));
        for (Character ch : characters) {
            builder.append(ch);
            dfs(map, digits, index + 1, builder);

            // 回溯：删除上次最佳的字符
            builder.deleteCharAt(builder.length() - 1);
        }
    }
}
