package learn.leetcode;

/**
 * 合并排序的数组
 *
 * 给定两个排序后的数组 A 和 B，其中 A 的末端有足够的缓冲空间容纳 B。 编写一个方法，将 B 合并入 A 并排序。
 *
 * 初始化 A 和 B 的元素数量分别为 m 和 n。
 *
 * 示例:
 *
 * 输入:
 * A = [1,2,3,0,0,0], m = 3
 * B = [2,5,6],       n = 3
 *
 * 输出: [1,2,2,3,5,6]
 * 说明:
 *
 * A.length == n + m
 *
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/sorted-merge-lcci
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author Zephyr
 * @date 2020/8/10.
 */
public class Easy022 {

    public static void main(String[] args) {
        int[] a = new int[]{2, 0};
        int[] b = {1};
        new Easy022().merge(a, 1, b, 1);
    }

    /**
     * 思路：从后向前合并，分别从两个数组的有效元素中按顺序取值
     */
    public void merge(int[] A, int m, int[] B, int n) {
        // 边界判断
        if (n == 0)
            return;
        if (m == 0 && n > 0) {
            System.arraycopy(B, 0, A, 0, n);
            return;
        }

        // 因为m和n的值为长度，要当下标使用，需要提前 减1
        m--; n--;
        // 定义下标i为结果数组的最后一个下标，从后向前合并，分别从两个数组的有效元素中按顺序取值
        // 直到其中一个数组的全部元素都合并到结果数组A为止
        int i = A.length-1;
        for (; i >= 0; i--) {
            if (m < 0 || n < 0)
                break;

            if (A[m] >= B[n]) {
                A[i] = A[m--];
            } else {
                A[i] = B[n--];
            }
        }
        // for循环结束后，如果数组B中还存在未被处理的元素，则将未被处理的元素复制到结果数组A
        if (n >= 0) {
            System.arraycopy(B, 0, A, 0, n + 1);
        }
    }
}
