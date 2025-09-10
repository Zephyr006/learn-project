package learn.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * 345. 反转字符串中的元音字母
 *
 * 给你一个字符串 s ，仅反转字符串中的所有元音字母，并返回结果字符串。
 *
 * 元音字母包括 'a'、'e'、'i'、'o'、'u'，且可能以大小写两种形式出现。
 *
 *  
 *
 * 示例 1：
 *
 * 输入：s = "hello"
 * 输出："holle"
 * 示例 2：
 *
 * 输入：s = "leetcode"
 * 输出："leotcede"
 *  
 *
 * 提示：
 *
 * 1 <= s.length <= 3 * 105
 * s 由 可打印的 ASCII 字符组成
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/reverse-vowels-of-a-string
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2021/8/17.
 */
public class Easy345 {

    public static void main(String[] args) {
        String s = "hello";
        System.out.println("result = " + new Solution().reverseVowels(s));
    }


    private static final class Solution {
        Set<Character> vowels = new HashSet<Character>(8) {{
            add('a');
            add('e');
            add('i');
            add('o');
            add('u');
            add('A');
            add('E');
            add('I');
            add('O');
            add('U');
        }};

        // 移动双指针，如果遇到两个指针都指向元音字母的情况，反转两个字符的位置
        public String reverseVowels(String s) {
            if (s.length() < 2) {
                return s;
            }
            char[] charArray = s.toCharArray();
            int length = charArray.length;
            int i = 0, j = length - 1;
            while (i < j) {
                boolean leftIsVowel = vowels.contains(charArray[i]);
                if (!leftIsVowel) {
                    i++;
                }
                boolean rightIsVowel = vowels.contains(charArray[j]);
                if (!rightIsVowel) {
                    j--;
                }
                if (leftIsVowel && rightIsVowel) {
                    char temp = charArray[i];
                    charArray[i] = charArray[j];
                    charArray[j] = temp;
                    i++;
                    j--;
                }
            }

            return new String(charArray);
        }

    }
}
