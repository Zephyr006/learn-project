package learn.leetcode;

/**
 * 374. 猜数字大小
 * https://leetcode.cn/problems/guess-number-higher-or-lower/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Easy374 {

    public int guessNumber(int n) {
        int left = 1, right = n;
        while (left <= right) {
            // mid 的取值写成 int mid = (left + right) / 2 ，会看到「力扣」给出「超出时间限制」的提示
            // 这是因为 left + right 超过了整形 int 的范围
            int mid = left + (right - left) / 2;
            int r = guess(mid);
            if (r == 0) {
                return mid;
            } else if (r > 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }

    private int guess(int n) {
        return 1;
    }
}
