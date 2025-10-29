package learn.leetcode;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 179. 最大数
 * https://leetcode.cn/problems/largest-number/description/
 */
public class Medium179 {
    public static void main(String[] args) {
        String s = new Medium179().largestNumber(new int[]{10, 2});
        System.out.println(s);
    }

    /**
     * 具体步骤通常是：
     *
     * 将数组中的所有整数转换为字符串。
     * 定义一个自定义的比较器（Comparator）或排序规则：比较两个字符串 a 和 b 时，比较 a + b 和 b + a 的字典序大小。如果 a + b > b + a，则 a 应该排在 b 前面。
     * 使用这个自定义规则对字符串数组进行排序。
     * 将排序后的所有字符串按顺序拼接起来。
     * （重要边界情况）如果拼接后的字符串以 '0' 开头（意味着所有数字都是 0），则直接返回 "0"，避免返回多个前导零如 "000"。
     */
    public String largestNumber(int[] nums) {
        String res = IntStream.of(nums).parallel().mapToObj(String::valueOf)
                .sorted((s1, s2) -> (s2 + s1).compareTo(s1 + s2))
                .collect(Collectors.joining());
        // 如果拼接后的字符串以 '0' 开头（意味着所有数字都是 0），则直接返回 "0"，避免返回多个前导零如 "000"。
        return res.startsWith("0") ? "0" : res;
    }
}
