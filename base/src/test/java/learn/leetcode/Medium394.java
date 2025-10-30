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
        int multi = 0;

        LinkedList<String> stackRes = new LinkedList<>();
        LinkedList<Integer> stackTimes = new LinkedList<>();
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);

            // 当 c 为 [ 时，将当前 multi 和 res 入栈，并分别置空置 0：
            // 记录此 [ 前的临时结果 res 至栈，用于发现对应 ] 后的拼接操作；
            // 记录此 [ 前的倍数 multi 至栈，用于发现对应 ] 后，获取 multi × [...] 字符串。
            // 进入到新 [ 后，res 和 multi 重新记录。
            if (ch == '[') {
                stackTimes.addLast(multi);
                stackRes.addLast(result.toString());
                multi = 0;
                result = new StringBuilder();
            }
            // 当 c 为 ']' 时，从栈中取出要拼接的次数 curr_times，用于字符串拼接
            // 拼接完成后，从字符串结果栈中取出拼接字符串前面的字符串，把两部分组装到一起
            else if (ch == ']') {
                // StringBuilder temp = new StringBuilder();
                String temp = result.toString();
                int curr_times = stackTimes.removeLast();
                for (int i1 = 1; i1 < curr_times; i1++) {
                    result.append(temp);
                }
                result.insert(0, stackRes.removeLast());
            }
            // 如果是数字，把数字信息记录到拼接次数 multi 中
            else if (ch >= '0' && ch <= '9') {
                multi = multi * 10 + ch - '0';
            }
            // 保存当前字符，有两种情况：1 不需要进行拼接，直接保存； 2 需要拼接，此时保存的字符是 [] 中间的字符，会在后面和 multi 一起用于拼接
            else {
                result.append(ch);
            }
        }
        return result.toString();
    }
}
