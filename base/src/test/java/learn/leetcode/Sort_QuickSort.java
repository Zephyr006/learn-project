package learn.leetcode;

/**
 * 快速排序
 *
 * 算法演示网站:https://www.cs.usfca.edu/~galles/visualization/Algorithms.html  https://visualgo.net/zh  https://algorithm-visualizer.org/
 *
 * @author Zephyr
 * @since 2022-03-15.
 */
public class Sort_QuickSort {

    public static void main(String[] args) {
        int[] array = {3, 2, 8, 4, 1, 5, 7, 0, 6};
        new Sort_QuickSort().quicksort(array, 0, array.length - 1);
        for (int i : array) {
            System.out.print("\t" + i);
        }
        System.out.println();
    }

    /**
     * ① 从数列中挑出一个元素，称为 “基准”（pivot），
     * ② 重新排序数列，所有元素比基准值小的摆放在基准前面，所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。在这个分区退出之后，该基准就处于数列的中间位置。这个称为分区（partition）操作。
     * ③ 递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序。
     * 如果您将其与归并排序进行比较，您会发现快速排序的划分步骤和解决步骤与归并排序完全相反。
     */
    private void quickSort(int[] array, int left, int right) {
        if (left < right) {
            //选择左边第一个元素作为中心元素
            int pivot = left;
            for (int i = left + 1; i <= right; i++) {
                // 如果中心元素的值小于其右一个元素的位置,则中心元素指针右移并且交换两个元素的位置,使右侧的元素值始终大于左侧的元素值
                if (array[i] > array[pivot]) {
                    pivot++;
                    // 位置在 pivot 和 i 上的元素交换位置
                    swap(array, pivot, i);
                }
            }

            swap(array, left, pivot);
            quickSort(array, left, pivot - 1);
            quickSort(array, pivot + 1, right);
        }
    }

    /**
     * Sort a subarray of a source array. The subarray is specified by its start
     * and end index.
     *
     * @param source the int array to be sorted
     * @param left   the start index of the subarray
     * @param right  the end index of the subarray
     */
    public static void quicksort(int[] source, int left, int right)
    {
        if (left < right) {
            int index = partition(source, left, right, right);
            quicksort(source, left, index - 1);
            quicksort(source, index + 1, right);
        }
    }

    /**
     * Split a subarray of a source array into two partitions. The left
     * partition contains elements that have value less than or equal to the
     * pivot element, the right partition contains the elements that have larger
     * value.
     *
     * @param source     the int array whose subarray will be splitted
     * @param left       the start position of the subarray
     * @param right      the end position of the subarray
     * @param pivotIndex the index of the pivot element inside the array
     * @return the new index of the pivot element inside the array
     */
    private static int partition(int[] source, int left, int right, int pivotIndex) {
        int pivot = source[pivotIndex];
        source[pivotIndex] = source[right];
        source[right] = pivot;

        int index = left;
        for (int i = left; i < right; i++) {
            if (source[i] <= pivot) {
                swap(source, index, i);
                index++;
            }
        }

        swap(source, index, right);
        return index;
    }

    private static void swap(int[] array, int a, int b) {
        if (a != b) {
            int temp = array[a];
            array[a] = array[b];
            array[b] = temp;
        }
    }
}
