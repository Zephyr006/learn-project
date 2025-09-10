package learn.leetcode;

/**
 * 1781. 所有子字符串美丽值之和
 * https://leetcode.cn/problems/sum-of-beauty-of-all-substrings/description/
 */
public class Medium1781 {

    /**
     * 暴力解：分别处理每一个子字符串，并记录数组中出现的字符次数，记录最大值和最小值
     * 技巧：由于 s 只包含小写字母，字符的出现次数可以使用数组存储
     */
    public int beautySum(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            int[] count = new int[26];
            int maxFeq = 0;
            for (int j = i; j < s.length(); j++) {
                count[s.charAt(j) - 'a']++;
                maxFeq = Math.max(maxFeq, count[s.charAt(j) - 'a']);
                int minFeq = Integer.MAX_VALUE;
                for (int k = 0; k < 26; k++){
                    if (count[k] > 0) {
                        minFeq = Math.min(minFeq, count[k]);
                    }
                }
                result += (maxFeq - minFeq);
            }
        }

        return result;
    }
}
