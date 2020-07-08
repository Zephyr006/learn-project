package learn.leetcode;

/**
 *  字符串转换整数 (atoi)
 *  请你来实现一个 atoi 函数，使其能将字符串转换成整数。
 *
 * 首先，该函数会根据需要丢弃无用的开头空格字符，直到寻找到第一个非空格的字符为止。接下来的转化规则如下：
 *
 * 如果第一个非空字符为正或者负号时，则将该符号与之后面尽可能多的连续数字字符组合起来，形成一个有符号整数。
 * 假如第一个非空字符是数字，则直接将其与之后连续的数字字符组合起来，形成一个整数。
 * 该字符串在有效的整数部分之后也可能会存在多余的字符，那么这些字符可以被忽略，它们对函数不应该造成影响。
 * 注意：假如该字符串中的第一个非空格字符不是一个有效整数字符、字符串为空或字符串仅包含空白字符时，则你的函数不需要进行转换，即无法进行有效转换。
 *
 * 在任何情况下，若函数不能进行有效的转换时，请返回 0 。
 *
 * 提示：
 *
 * 本题中的空白字符只包括空格字符 ' ' 。
 * 假设我们的环境只能存储 32 位大小的有符号整数，那么其数值范围为 [−231,  231 − 1]。如果数值超过这个范围，请返回  INT_MAX (231 − 1) 或 INT_MIN (−231) 。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/string-to-integer-atoi
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 * @author Zephyr
 * @date 2020/7/1.
 */
public class Medium001 {

    public static void main(String[] args) {
        Integer integer = Integer.valueOf('0');
        System.out.println(new Medium001().myAtoi("+0 123"));
    }


    public int myAtoi(String str) {
        int len = str.length();
        // str.charAt(i) 方法回去检查下标的合法性，一般先转换成字符数组
        char[] charArray = str.toCharArray();

        // 1、去除前导空格
        int index = 0;
        while (index < len && charArray[index] == ' ') {
            index++;
        }

        // 2、如果已经遍历完成（针对极端用例 "      "）
        if (index == len) {
            return 0;
        }

        // 3、如果出现符号字符，仅第 1 个有效，并记录正负
        int sign = 1;
        char firstChar = charArray[index];
        if (firstChar == '+') {
            index++;
        } else if (firstChar == '-') {
            index++;
            sign = -1;
        }

        // 4、将后续出现的数字字符进行转换
        // 不能使用 long 类型，这是题目说的
        int res = 0;
        while (index < len) {
            char currChar = charArray[index];
            // 4.1 先判断不合法的情况
            if (currChar > '9' || currChar < '0') {
                break;
            }

            // 题目中说：环境只能存储 32 位大小的有符号整数，因此，需要提前判断乘以 10 以后是否越界
            if (res > Integer.MAX_VALUE / 10 || (res == Integer.MAX_VALUE / 10 && (currChar - '0') > Integer.MAX_VALUE % 10)) {
                return Integer.MAX_VALUE;
            }
            if (res < Integer.MIN_VALUE / 10 || (res == Integer.MIN_VALUE / 10 && (currChar - '0') > -(Integer.MIN_VALUE % 10))) {
                return Integer.MIN_VALUE;
            }

            // 4.2 合法的情况下，才考虑转换，每一步都把符号位乘进去
            res = res * 10 + sign * (currChar - '0');
            index++;
        }
        return res;


        //以下是自己写的解、不能处理 "+0 123" 这样的情况
        //Boolean negative = null;
        //boolean numBegin = false;
        //long result = 0;
        //Character firstChar = null;
        //
        //for (int i = 0; i < str.length(); i++) {
        //    char c = str.charAt(i);
        //    if (c != ' ') {
        //        if (firstChar == null) {
        //            if (c != '-' && c != '+' && (c < '0' || c > '9')) {
        //                return 0;
        //            }
        //            firstChar = c;
        //        }
        //        if (numBegin && (c < '0' || c > '9'))
        //            break;
        //        else if (c == '-') {
        //            if (negative != null)
        //                break;
        //            negative = true;
        //        }
        //        else if (c == '+') {
        //            if (negative != null)
        //                break;
        //            negative = false;
        //        }
        //        else if (c >= '0' && c <= '9') {
        //            numBegin = true;
        //            result = result * 10 + (c - '0');
        //        }
        //        else
        //            return 0;
        //    }
        //    else {
        //        if (negative != null || numBegin == true)
        //            return 0;
        //    }
        //}
        //if (negative != null && negative) {
        //    result = -result;
        //}
        //if (result > Integer.MAX_VALUE) {
        //    return Integer.MAX_VALUE;
        //}
        //if (result < Integer.MIN_VALUE) {
        //    return Integer.MIN_VALUE;
        //}
        //return (int) result;
    }
}
