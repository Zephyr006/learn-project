package learn.leetcode;

public class Test {

    public static void main(String[] args) {


    }

    // 翻转字符串，如果某个单词是特定字符串，则不翻转
    public String reverse(String input, String spec) {
         // = "Welcome to fliggy!";
         // = "fliggey";

        boolean isNull = spec == null || spec.isEmpty();
        Character lastCh = isNull ? null : spec.charAt(spec.length() - 1);
        StringBuilder builder = new StringBuilder();
        for (int i = input.length() - 1; i >= 0; i--) {
            if (!isNull && input.charAt(i) == lastCh) {
                // 判断当前子串是否和指定字符串严格相等
                int j = i, k = spec.length() - 1;
                while (k >= 0 && input.charAt(j) == spec.charAt(k)) {
                    j--;
                    k--;
                }
                // 发现完全匹配，直接保存
                if (k < 0) {
                    builder.append(spec);
                    i = i - spec.length() + 1;
                }
            } else {
                builder.append(Character.toUpperCase(input.charAt(i)));
            }
        }
        return builder.toString();
    }
}
