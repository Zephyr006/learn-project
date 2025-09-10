package learn.leetcode;

import java.util.ArrayList;
import java.util.List;

/**
 * n 皇后问题
 * https://leetcode.cn/problems/n-queens/
 */
public class Hard51 {
    public static void main(String[] args) {
        List<List<String>> lists = new Hard51().solveNQueens(4);
    }

    public List<List<String>> solveNQueens2(int n) {
        int[] tokenIdxArray = new int[n];
        dfs(n, 0, tokenIdxArray);
        return res;
    }


    List<List<String>> res = new ArrayList<>();
    public List<List<String>> solveNQueens(int n) {
        int[] tokenIdxArray = new int[n];
        dfs(n, 0, tokenIdxArray);
        return res;
    }

    void dfs(int n, int lineIdx, int[] tokenIdxArray) {
        // 1 结束条件
        if (lineIdx >= n) {
            // 由于不符合题意的排列在前面的流程中已经被剪枝（过滤）了，
            // 所以能偶倒结束条件的排列组合一定是满足题意的，不需要再校验
            // if (isValid(tokenIdxArray, lineIdx)) {
                addAnswer(tokenIdxArray);
            // }
            return;
        }

        // 2 dfs：摆当前行的皇后，如果满足条件的话，继续递归
        for (int i = 0; i < n; i++) {
            tokenIdxArray[lineIdx] = i;
            if (isValid(tokenIdxArray, lineIdx)) {
                dfs(n, lineIdx + 1, tokenIdxArray);
            }

            // 3 回溯：tokenIdxArray的对应位置归 0 (当前实现不需要，主要是为了规范和理解)
            tokenIdxArray[lineIdx] = 0;
        }
    }

    boolean isValid(int[] tokenIdxArray, int lineIdx) {
        for (int i = 0; i < lineIdx; i++) {
            // 前面的行摆放的皇后有和当前行摆放的在相同位置，不满足
            if (tokenIdxArray[i] == tokenIdxArray[lineIdx] ||
                    // 或者前面行行号的差值等于摆放位置的差值，说明处于对角线，不满足
                    // 取绝对值是为了处理左侧对角线和右侧对角线两种情况
                    lineIdx - i == Math.abs(tokenIdxArray[i] - tokenIdxArray[lineIdx])) {
                return false;
            }
        }
        return true;
    }

    void addAnswer(int[] tokenIdxArray) {
        List<String> list = new ArrayList<>();

        for (int idx : tokenIdxArray) {
            char[] chars = new char[tokenIdxArray.length];
            for (int i = 0; i < tokenIdxArray.length; i++) {
                if (i == idx) {
                    chars[i] = 'Q';
                } else {
                    chars[i] = '.';
                }
            }
            list.add(String.valueOf(chars));
        }
        res.add(list);
    }
}
