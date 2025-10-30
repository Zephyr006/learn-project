package learn.leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * 79. 单词搜索
 * https://leetcode.cn/problems/word-search/description/?envType=study-plan-v2&envId=top-100-liked
 */
public class Medium79 {
    public static void main(String[] args) {
        boolean exist = new Medium79().exist(LeetcodeHelper.to2DCharArray(
                "[[\"A\",\"B\",\"C\",\"E\"],[\"S\",\"F\",\"C\",\"S\"],[\"A\",\"D\",\"E\",\"E\"]]"),
                "SEE");
        System.out.println(exist);
    }

    Set<String> used = new HashSet<>();
    public boolean exist(char[][] board, String word) {
        // 遍历每一个起点
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[0].length; j++) {
                if (dfs(board, word, 0, i, j))
                    return true;
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, String word, int wordIdx, int level, int offset) {
        // loc 标识当前位置，避免遍历时走回头路
        // 判断遍历是否越界，或者已经遍历过，或者当前位置值不等于单词中对应位置的值
        String loc = level + "-" + offset;
        if (level < 0
                || offset < 0
                || level >= board.length
                || offset >= board[0].length
                || used.contains(loc)
                || board[level][offset] != word.charAt(wordIdx)) {
            return false;
        }

        // 如果已经遍历结束，说明都匹配，返回 true。没有遍历结束的话，就记录当前位置 已遍历
        if (wordIdx == word.length() - 1) {
            return true;
        }
        used.add(loc);

        // 遍历上下左右四个方向的相邻单元格，查找是否能继续匹配单词 word
        boolean res = dfs(board, word, wordIdx + 1, level, offset + 1)
                || dfs(board, word, wordIdx + 1, level, offset - 1)
                || dfs(board, word, wordIdx + 1, level + 1, offset)
                || dfs(board, word, wordIdx + 1, level - 1, offset);

        // 回溯：删除已遍历的标记
        used.remove(loc);
        return res;
    }
}
