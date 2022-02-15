package learn.example.javase.leetcode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
 *
 * 示例 1:
 *
 * 输入: "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。
 * 示例 2:
 *
 * 输入: "bbbbb"
 * 输出: 1
 * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。
 * 示例 3:
 *
 * 输入: "pwwkew"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
 *      请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/longest-substring-without-repeating-characters
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @author Zephyr
 * @date 2020/6/30.
 */
public class Easy002 {

    public static void main(String[] args) {
        String s = "asdfghjj";
        System.out.println(new Easy002().lengthOfLongestSubstring(s));


        HashMap<Character, Integer> map = new HashMap<Character, Integer>();
        int max = 0;
        int left = 0;
        for(int i = 0; i < s.length(); i ++){
            if(map.containsKey(s.charAt(i))){
                // 滑动窗口的重点：将left指针移动到重复元素的右侧
                left = Math.max(left, map.get(s.charAt(i)) + 1);
            }
            map.put(s.charAt(i),i);
            max = Math.max(max,i-left+1);
        }
        System.out.println(max);
    }

    /**
     * 暴力枚举法：找出 从每一个字符开始的，不包含重复字符的最长子串，那么其中最长的那个字符串即为答案。
     * 自己写的实现
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring(String s) {
        Set<Character> set = new HashSet<>();
        int maxLen=0;
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                char c = s.charAt(j);
                if (set.contains(c)) {
                    maxLen = Math.max(maxLen, set.size());
                    set.clear();
                    break;
                }
                set.add(c);
            }
        }
        return Math.max(maxLen, set.size());
    }

    /**
     * 滑动窗口方法: map (k, v)，其中 key 值为字符，value 值为字符下标位置
     */
    public int lengthOfLongestSubstring3(String s) {
        int max = 0;
        Map<Character, Integer> map = new HashMap<>();
        for(int left = 0, right = 0; right < s.length(); right++){
            char element = s.charAt(right);
            if(map.containsKey(element)){
                left = Math.max(map.get(element) + 1, left); //map.get()的地方进行+1操作
            }
            max = Math.max(max, right - left + 1);
            map.put(element, right);
        }
        return max;
    }
    /**
     * 官方写的实现
     * @param s
     * @return
     */
    public int lengthOfLongestSubstring2(String s) {
        // 哈希集合，记录每个字符是否出现过
        Set<Character> occ = new HashSet<Character>();
        int n = s.length();
        // 右指针，初始值为 -1，相当于我们在字符串的左边界的左侧，还没有开始移动
        int rk = -1, ans = 0;
        for (int i = 0; i < n; ++i) {
            if (i != 0) {
                // 左指针向右移动一格，移除一个字符
                occ.remove(s.charAt(i - 1));
            }
            while (rk + 1 < n && !occ.contains(s.charAt(rk + 1))) {
                // 不断地移动右指针
                occ.add(s.charAt(rk + 1));
                ++rk;
            }
            // 第 i 到 rk 个字符是一个极长的无重复字符子串
            ans = Math.max(ans, rk - i + 1);
        }
        return ans;
    }


}
