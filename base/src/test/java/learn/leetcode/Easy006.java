package learn.leetcode;

/**
 * 14. 最长公共前缀
 *
 * 编写一个函数来查找字符串数组中的最长公共前缀。
 *
 * 如果不存在公共前缀，返回空字符串 ""。
 *
 * 示例 1:
 *
 * 输入: ["flower","flow","flight"]
 * 输出: "fl"
 * 示例 2:
 *
 * 输入: ["dog","racecar","car"]
 * 输出: ""
 * 解释: 输入不存在公共前缀。
 * 说明:
 *
 * 所有输入只包含小写字母 a-z 。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-common-prefix
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @since 2020-07-03.
 */
public class Easy006 {
    public static void main(String[] args) {
        String[] strings = new String[]{};
        String longestCommonPrefix = new Easy006().longestCommonPrefix(strings);
        System.out.println(longestCommonPrefix);
    }

    public String longestCommonPrefixOfficial(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        int length = strs[0].length();
        int count = strs.length;
        for (int i = 0; i < length; i++) {
            char c = strs[0].charAt(i);
            for (int j = 1; j < count; j++) {
                if (i == strs[j].length() || strs[j].charAt(i) != c) {
                    return strs[0].substring(0, i);
                }
            }
        }
        return strs[0];

    }

    /*
    纵向扫描。纵向扫描时，从前往后遍历所有字符串的每一列，比较相同列上的字符是否相同，
    如果相同则继续对下一列进行比较，如果不相同则当前列不再属于公共前缀，当前列之前的部分为最长公共前缀。
     */
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }
        StringBuilder commonStr = new StringBuilder();
        try {
            // 第一层循环，从第0个字符开始循环判断，直到遇到数组最短的一个遍历完成
            // （charAt方法会抛出StringIndexOutOfBoundsException）
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                // 改进：currentChar直接等于数组中第一个字符串的对应字符，减少一次循环
                char currentChar = strs[0].charAt(i);
                //第二层循环，判断字符串数组中每个字符串的对应位置字符是否相同
                for (int j = 1; j < strs.length; j++) {
                    if (currentChar != strs[j].charAt(i)) {
                        return String.valueOf(commonStr);
                    }
                }
                commonStr.append(currentChar);
            }
        } catch (StringIndexOutOfBoundsException e) {

        }

        return String.valueOf(commonStr);
    }
}
