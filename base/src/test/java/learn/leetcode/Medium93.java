package learn.leetcode;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 93. 复原 IP 地址
 * https://leetcode.cn/problems/restore-ip-addresses/description/
 */
public class Medium93 {
    public static void main(String[] args) {
        List<String> res = new Medium93().restoreIpAddresses("1234");
        LeetcodeHelper.print(res);
    }

    List<String> res = new ArrayList<>();
    Deque<String> path = new ArrayDeque<>(4);
    /**
     * 回溯算法：参考https://leetcode.cn/problems/restore-ip-addresses/solutions/100433/hui-su-suan-fa-hua-tu-fen-xi-jian-zhi-tiao-jian-by/
     */
    public List<String> restoreIpAddresses(String s) {
        //特殊情况判断：如果当前字符长度大于12或者小于4都不满足
        if (s.length() < 4 || s.length() > 12) {
            return res;
        }

        dfs(s, 0, 4);
        return res;
    }

    /**
     * @param beginIdx 字符串中的开始下标位置
     * @param leftSegCount 剩余需要的 ip 段，总共需要 4 个
     */
    void dfs(String s, int beginIdx , int leftSegCount){
        // 1 结束条件：已经遍历到了字符串的结尾
        if (beginIdx == s.length()) {
            // 结束时，判断是否满足题目要求，保存满足要求的答案
            if (leftSegCount == 0) {
                res.add(String.join(".", path));
            }
            return;
        }

        // 2 dfs：截取当前段的子字符串，如果当前子字符串满足要求，则暂存当前子串，并继续 dfs 递归处理
        for (int i = beginIdx; i < s.length() && i < beginIdx + 3; i++) {
            // 剪枝：如果字符串剩余长度超过正常的 ip 所需长度限制（每个 ip 段 最多 3 个字符）
            if (s.length() - 1 - i > 3 * leftSegCount) {
                continue;
            }
            String subSeg = s.substring(beginIdx, i + 1);
            if (isValidIPSeg(subSeg)) {
                path.add(subSeg);
                dfs(s, i + 1, leftSegCount - 1);

                // 3 当前分支已经处理完，把状态修改为原来的状态
                path.removeLast();
            }
        }
    }

    private boolean isValidIPSeg(String seg) {
        if (seg.isEmpty() ||
                (seg.charAt(0) == '0' && seg.length() > 1)) {
            return false;
        }
        int i = Integer.parseInt(seg);
        return i >= 0 && i <= 255;
    }

}
