package learn.example.javase.leetcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 美团校招 遍历字符串判断是否合法
 * 链接：https://leetcode-cn.com/leetbook/read/meituan/ohsjgd
 *
 * @author Zephyr
 * @date 2022/2/26.
 */
public class Easy024 {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        int t = Integer.parseInt(reader.readLine());
        while (t-- != 0) {
            //char[] s = reader.readLine().toCharArray();

            if (!isIllegal(reader.readLine()))
                writer.write("Accept\n");
            else
                writer.write("Wrong\n");
        }

        writer.flush();
        reader.close();
        writer.close();
    }

    private static boolean isIllegal(String s) {
        char[] chars = s.toCharArray();
        int length = chars.length;
        char firstChar = chars[0];
        if (!Boolean.logicalOr(firstChar >= 'a' && firstChar <= 'z', firstChar >= 'A' && firstChar <= 'Z')) {
            return true;
        }

        boolean hasNumber = false;
        for (int i = 1; i < length; i++) {
            boolean digit = Character.isDigit(chars[i]);
            if (!digit && !Character.isLetter(chars[i])) {
                return true;
            }
            hasNumber |= digit;
        }
        return !hasNumber;
    }



}
