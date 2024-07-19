package learn.leetcode;

/**
 * 版本号对比
 * https://leetcode.cn/problems/compare-version-numbers/description/
 */
public class Medium165 {

    public static void main(String[] args) {
        LeetcodeHelper.invokePublicMethods("1.0", "1.0.0.0");
    }

    /**
     * 模拟
     */
    public int compareVersion(String version1, String version2) {
        String[] strArr1 = version1.split("\\.");
        String[] strArr2 = version2.split("\\.");
        int longLen = Math.max(strArr1.length, strArr2.length);

        for (int i = 0; i < longLen; i++) {
            int v1 = strArr1.length > i ? Integer.parseInt(strArr1[i]) : 0;
            int v2 = strArr2.length > i ? Integer.parseInt(strArr2[i]) : 0;
            if (v1 > v2) {
                return 1;
            } else if (v1 < v2) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * 双指针解法
     */
    public int compareVersionDoublePointer(String version1, String version2) {
        int p_1 = 0, p_2 = 0;

        while (p_1 < version1.length() || p_2 < version2.length()) {
            int sum1 = 0;
            while (p_1 < version1.length() && version1.charAt(p_1) != '.') {
                sum1 = sum1 * 10 + (version1.charAt(p_1) - '0');
                p_1++;
            }
            p_1++; //跳过点号

            int sum2 = 0;
            while (p_2 < version2.length() && version2.charAt(p_2) != '.') {
                sum2 = sum2 * 10 + (version2.charAt(p_2) - '0');
                p_2++;
            }
            p_2++; //跳过点号

            if (sum1 != sum2) {
                return sum1 > sum2 ? 1 : -1;
            }
        }
        return 0;
    }


}
