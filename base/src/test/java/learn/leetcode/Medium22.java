package learn.leetcode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 22. 括号生成
 * https://leetcode.cn/problems/generate-parentheses/description/
 */
public class Medium22 {
    public static void main(String[] args) {
        List<String> strings = new Medium22().generateParenthesis(2);
        System.out.println(strings);
    }

    List<String> result = new ArrayList<>();
    public List<String> generateParenthesis(int n) {
        dfs(new StringBuilder(), n, n);
        return result;
    }

    private void dfs(StringBuilder builder, int left, int right) {
        // 结束条件：所有括号都已经正确的放置到字符串中
        if (left > right) {
            return;
        }
        if (left == 0 && right == 0) {
            String s = builder.toString();
            // if (isValid(s)) {
                result.add(s);
            // }
            return;
        }

        // dfs:分别尝试在当前文职加左括号或者右括号
        if (left > 0) {
            builder.append("(");
            dfs(builder, left - 1, right);
            // 回溯
            builder.deleteCharAt(builder.length() - 1);
        }
        if (right > 0) {
            builder.append(")");
            dfs(builder, left, right - 1);
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    @Deprecated
    private boolean isValid(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }
        LinkedList<Character> stack = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty()) {
                    return false;
                } else {
                    stack.pop();
                }
            }
        }
        return stack.isEmpty();
    }

}
