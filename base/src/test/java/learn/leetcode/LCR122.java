package learn.leetcode;

/**
 * 路径加密
 * https://leetcode.cn/problems/ti-huan-kong-ge-lcof/description/?envType=study-plan-v2&envId=coding-interviews
 */
public class LCR122 {

    public String pathEncryption(String path) {
        StringBuilder builder = new StringBuilder();
        for (char ch : path.toCharArray()) {
            if (ch == '.') {
                builder.append(' ');
            } else
                builder.append(ch);
        }
        return builder.toString();
    }

}
