package learn.leetcode;

/**
 * 875. 爱吃香蕉的珂珂
 * https://leetcode.cn/problems/koko-eating-bananas/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium875 {
    public static void main(String[] args) {
        int k = new Medium875().minEatingSpeed(LeetcodeHelper.toIntArray("[3,6,7,11]"), 8);
        System.out.println(k);
    }

    /**
     * 二分查找：每次吃的香蕉数量最少是 1 个，最多是数组中的最大元素个数（根据题意，一次吃一堆香蕉，不能更快了），正确答案肯定在这个区间内
     * 一个一个 k 值尝试也是可以的，但是为了加速查找过程，使用二分查找的方法
     */
    public int minEatingSpeed(int[] piles, int h) {
        // 首先确定查找区间的左右边界
        int left = 1, right = 1;
        for (int p : piles) {
            right = Math.max(right, p);
        }

        // 在区间内执行二分查找
        while (left < right) {
            int mid = left + (right - left) / 2;
            // 如果可以满足题意，即在 mid 时间内能吃完，说明正确答案在 mid 的左区间，否则在 mid 的右区间
            if (needH(piles, mid) <= h) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    private int needH(int[] piles, int k) {
        int h = 0;
        for (int p : piles) {
            int times = p / k;
            if (times * k < p) {
                times++;
            }
            h += times;
        }
        return h;
    }
}
