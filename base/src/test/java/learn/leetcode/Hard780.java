package learn.leetcode;

/**
 * 780. 到达终点
 * 给定四个整数 sx , sy ，tx 和 ty，如果通过一系列的转换可以从起点 (sx, sy) 到达终点 (tx, ty)，则返回 true，否则返回 false。
 *
 * 从点 (x, y) 可以转换到 (x, x+y)  或者 (x+y, y)。
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/reaching-points
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2022/4/10.
 */
public class Hard780 {
    public static void main(String[] args) {
        boolean b = new Solution().reachingPoints(1, 1, 2,2);
        System.out.println(b);
    }

    static class Solution {
        public boolean reachingPoints(int sx, int sy, int tx, int ty) {
            while (tx != ty && sx < tx && sy < ty) {
                if (tx < ty) {
                    ty = ty - tx;
                } else {
                    tx = tx - ty;
                }
            }
            // 满足条件。返回true
            if (tx == sx && ty == sy) {
                return true;
            //x轴的值相等，判断y轴的值能否在n次相减后与sy相等即可
            } else if (tx == sx) {
                return ty > sy && (ty - sy) % tx == 0;
            //y轴的值相等，判断x轴的值能否在n次相减后与sx相等即可
            } else if (ty == sy) {
                return tx > sx && (tx - sx) % ty == 0;
            // 前面的if条件都不满足，则说明转换后无法到达重点，返回false
            } else {
                return false;
            }

        }
    }
}
