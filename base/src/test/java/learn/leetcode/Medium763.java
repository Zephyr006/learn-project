package learn.leetcode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 763. 划分字母区间
 * https://leetcode.cn/problems/partition-labels/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium763 {

    /**
     * 贪心算法：
     */
    public List<Integer> partitionLabels(String s) {
        // 用 map 记录每个字符出现最后一次出现的下标位置
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            map.put(s.charAt(i), i);
        }

        // 记录每个片段中的开始下标和结束下标
        int segIdxStart = 0;
        int segIdxLast = 0;
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            Integer lastIdxForC = map.get(s.charAt(i));
            // 肯定到有一个时间，这个last就更新不了了，那么这个时候这个位置就是我们的分隔位置。
            segIdxLast = Math.max(segIdxLast, lastIdxForC);

            // segIdxLast等于当前下标位置，说明前面的字符都已经出现过，满足题意，记录答案
            if (segIdxLast == i) {
                result.add(segIdxLast - segIdxStart + 1);
                segIdxStart = i + 1;
            }
        }
        return result;
    }
}
