package learn.leetcode;

/**
 * LCR 138. 有效数字
 * https://leetcode.cn/problems/biao-shi-shu-zhi-de-zi-fu-chuan-lcof/description
 */
public class LCR138 {

    public static void main(String[] args) {
        //LeetcodeHelper.invokePublicMethods(".e1");
        //LeetcodeHelper.invokePublicMethods("0e");
        //LeetcodeHelper.invokePublicMethods("2e0");
        LeetcodeHelper.invokePublicMethods(" -.");
    }

    public boolean validNumber(String s) {
        String str = s.trim();
        if (str.isEmpty()) {
            return false;
        }
        int idx = 0;
        // + -
        if (str.charAt(idx) == '+' || str.charAt(idx) == '-') {
            idx++;
        } else if (!isNumber(str.charAt(idx)) && idx == str.length() - 1) { // ".3"
            return false;
        }
        // number    ---  ".1" is valid number
        //if (idx >= str.length() || !isNumber(str.charAt(idx))) {
        //    return false;
        //}
        //int
        while (idx < str.length() && isNumber(str.charAt(idx))) {
            idx++;
        }
        if (idx >= str.length()) {
            return true;
        }

        // . or E
        char pointOrE = Character.toUpperCase(str.charAt(idx));
        if (pointOrE != 'E' && pointOrE != '.') {
            return false;
        }

        if (pointOrE == '.') {
            // if end with . and preview char is number
            if (idx == str.length() - 1 && idx != 0 && isNumber(str.charAt(idx - 1))) {
                return true;
            }
            idx++;
            int xiaoshuStart = idx;
            while (idx < str.length() && isNumber(str.charAt(idx))) {
                idx++;
            }
            if (idx - xiaoshuStart == 0) {
                return false;
            }
        }
        // is end
        if (idx == str.length()) {
            return true;
        }
        // has more and not E
        if (Character.toUpperCase(str.charAt(idx)) != 'E' || idx == 0) {  // eg. "e9"
            return false;
        }
        idx++; // skip E
        if (idx >= str.length()) { // end with E is invalid
            return false;
        }

        // 跳过 E 后面的 - +
        if (str.charAt(idx) == '+' || str.charAt(idx) == '-') {
            idx++;
        } else if (!isNumber(str.charAt(idx)) && idx == str.length() - 1) { // ".3"
            return false;
        }

        // E must with more number
        if (idx >= str.length() || !isNumber(str.charAt(idx))) {
            return false;
        }
        while (idx < str.length() && isNumber(str.charAt(idx))) {
            idx++;
        }
        // here it should be end
        if (idx < str.length()) {
            return false;
        }
        return true;
    }

    private boolean isNumber(char ch) {
        return !(ch > '9' || ch < '0');
    }

}
