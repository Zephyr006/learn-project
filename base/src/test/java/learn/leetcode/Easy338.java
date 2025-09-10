package learn.leetcode;

/**
 * 338. 比特位计数
 * https://leetcode.cn/problems/counting-bits/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Easy338 {

    public int[] countBits(int n) {
        int[] one_arr = new int[32];
        for (int i = 1; i < 32; i++) {
            int temp = 1 << i;
            if (temp >= Math.abs(n)) {
                one_arr[i] = temp;
            }
        }

        
        return one_arr;
    }
}
