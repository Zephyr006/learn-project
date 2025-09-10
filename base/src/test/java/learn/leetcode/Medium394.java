package learn.leetcode;

import java.util.LinkedList;

/**
 * 394. 字符串解码
 * <p>
 * https://leetcode.cn/problems/decode-string/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium394 {
    public static void main(String[] args) {
        if (!"accaccacc".equals(new Medium394().decodeString("3[a2[c]]"))) {
            System.err.println("error");
        }
    }

    public String decodeString(String s) {
        StringBuilder result = new StringBuilder();
        int num = 0;

        LinkedList<String> stackRes = new LinkedList<>();
        LinkedList<Integer> stackTimes = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            // 当 c 为 [ 时，将当前 multi 和 res 入栈，并分别置空置 0：
            // 记录此 [ 前的临时结果 res 至栈，用于发现对应 ] 后的拼接操作；
            // 记录此 [ 前的倍数 multi 至栈，用于发现对应 ] 后，获取 multi × [...] 字符串。
            // 进入到新 [ 后，res 和 multi 重新记录。
            if (ch == '[') {
                stackTimes.addLast(num);
                stackRes.addLast(result.toString());
                num = 0;
                result = new StringBuilder();
            }
            else if (ch == ']') {
                StringBuilder temp = new StringBuilder();
                int curr_times = stackTimes.removeLast();
                for (int i1 = 0; i1 < curr_times; i1++) {
                    temp.append(result);
                }
                result = new StringBuilder(stackRes.removeLast() + temp);
            }
            else if (ch >= '0' && ch <= '9') {
                num = num * 10 + ch - '0';
            }
            else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
