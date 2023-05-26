package learn.leetcode;

/**
 * 28. 实现 strStr()
 *
 * 给定一个 haystack 字符串和一个 needle 字符串，在 haystack 字符串中找出 needle 字符串出现的第一个位置 (从0开始)。如果不存在，则返回  -1。
 *
 * 示例 1:
 *
 * 输入: haystack = "hello", needle = "ll"
 * 输出: 2
 * 示例 2:
 *
 * 输入: haystack = "aaaaa", needle = "bba"
 * 输出: -1
 * 说明:
 *
 * 当 needle 是空字符串时，我们应当返回什么值呢？这是一个在面试中很好的问题。
 *
 * 对于本题而言，当 needle 是空字符串时我们应当返回 0 。这与C语言的 strstr() 以及 Java的 indexOf() 定义相符。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/implement-strstr
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @since 2020-7/4.
 */
public class Easy011 {

    public static void main(String[] args) {
        new Easy011().strStr("h", "h");
    }


    public int strStr(String haystack, String needle) {
        if (needle == null || "".equals(needle))
            return 0;

        char[] mainCharArray = haystack.toCharArray();
        char[] subCharArray = needle.toCharArray();

        // i <= mainCharArray.length - subCharArray.length
        for (int i = 0; i < mainCharArray.length - subCharArray.length + 1; i++) {
            // here : j=0
            for (int j = 0; j < subCharArray.length; j++) {
                if (mainCharArray[i+j] != subCharArray[j]) {
                    break;
                }
                if (mainCharArray[i+j] == subCharArray[j] && j == subCharArray.length - 1) {
                    return i;
                }

            }
        }
        return -1;
    }
}
