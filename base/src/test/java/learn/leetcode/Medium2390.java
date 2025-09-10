package learn.leetcode;

import java.util.Stack;

/**
 * 2390. 从字符串中移除星号
 *
 * https://leetcode.cn/problems/removing-stars-from-a-string/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium2390 {
    public static void main(String[] args) {
        String removeStars = new Medium2390().removeStars("leet**cod*e");
        System.out.println(removeStars);
    }

    public String removeStars(String S) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < S.length(); i++) {
            char c = S.charAt(i);
            if (c != '*') {
                stack.push(c);
            } else {
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        while (!stack.isEmpty()) {
            sb.insert(0, stack.pop());
        }
        return sb.toString();
    }

    /**
     * 栈：由于是要移除星号左侧的字符，所以需要从右向左处理，涉及到反转顺序的问题就用栈
     * 记录右侧的连续星号个数，如果不为星号，则对应字符不放到结果集，最终返回翻转之后的结果（因为是逆序处理的）
     */
    public String removeStarsForce(String s) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            stack.push(s.charAt(i));
        }

        int starCount = 0;
        StringBuilder sb = new StringBuilder(s.length());
        while (!stack.isEmpty()) {
            Character character = stack.pop();
            if (character == '*') {
                starCount++;
            } else if (starCount == 0) {
                sb.append(character);
            } else {
                starCount--;
            }
        }
        return sb.reverse().toString();
    }
}
