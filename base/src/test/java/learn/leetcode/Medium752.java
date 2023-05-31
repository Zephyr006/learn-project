package learn.leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 752. 打开转盘锁
 * { @link https://leetcode.cn/problems/open-the-lock/}
 *
 * @author Zephyr
 * @since 2023-05-30
 */
public class Medium752 {

    public static void main(String[] args) {
        char c = '0';
        char res = (char)((int)c + 1);
        System.out.println(res);
    }

    static class Solution {

        public int openLock(String[] deadends, String target) {
            Set<String> dead = new HashSet<>(Arrays.asList(deadends));
            Set<String> used = new HashSet<>();


            return -1;
        }

        List<String> possibleWay(String nowPoint, Set<String> deadends) {
            char[] chars = nowPoint.toCharArray();
            List<String> res = new ArrayList<>(8);
            for (int i = 0; i < 4; i++) {
                chars[i] = (char) ((int)chars[i] + 1);
                String s = new String(chars);

                if (!deadends.contains(s)) {
                    res.add(s);
                }
                chars[i] = (char) ((int)chars[i] - 1);
                s = new String(chars);
                if (!deadends.contains(s)) {
                    res.add(s);
                }
            }
            return res;
        }
    }
}
