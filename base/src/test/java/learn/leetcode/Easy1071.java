package learn.leetcode;

/**
 * 对于字符串 s 和 t，只有在 s = t + t + t + ... + t + t（t 自身连接 1 次或多次）时，我们才认定 “t 能除尽 s”。
 *
 * 给定两个字符串 str1 和 str2 。返回 最长字符串 x，要求满足 x 能除尽 str1 且 x 能除尽 str2 。
 *
 *
 *
 * 示例 1：
 *
 * 输入：str1 = "ABCABC", str2 = "ABC"
 * 输出："ABC"
 * 示例 2：
 *
 * 输入：str1 = "ABABAB", str2 = "ABAB"
 * 输出："AB"
 * 示例 3：
 *
 * 输入：str1 = "LEET", str2 = "CODE"
 * 输出：""
 *
 *
 * 提示：
 *
 * 1 <= str1.length, str2.length <= 1000
 * str1 和 str2 由大写英文字母组成
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/greatest-common-divisor-of-strings
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Easy1071 {

    public static void main(String[] args) {
        String str1 = "TAUXXTAUXXTAUXXTAUXXTAUXX", str2 = "TAUXXTAUXXTAUXXTAUXXTAUXXTAUXXTAUXXTAUXXTAUXX";
        System.out.println(new Easy1071().gcdOfStrings(str1, str2));
        System.out.println(new Easy1071().gcdOfStrings(str1, str2).length());
    }

    public String gcdOfStrings(String str1, String str2) {
        if (str1.length() < str2.length()) {
            return gcdOfStrings(str2, str1);
        }
        int str1Length = str1.length();
        for (int i = 0; i < str2.length(); i++) {
            int subLen = str2.length() - i;
            //  如果子串不能被** str1 或 str2 **整除，则肯定不能除尽，跳过
            if (str1Length % subLen != 0 || str2.length() % subLen != 0) {
                continue;
            }

            String substring = str2.substring(0, subLen);
            // 要求满足 x 能除尽 str1 且 x 能除尽 str2 。
            if (isGCD(str1, substring) && isGCD(str2, substring)) {
                return substring;
            }
        }
        return "";
    }

    private boolean isGCD(String str1, String str2) {
        for (int i = 0; i < str1.length() / str2.length(); i++) {
            if (!str1.startsWith(str2, i * str2.length())) {
                return false;
            }
        }
        return true;
    }
}
