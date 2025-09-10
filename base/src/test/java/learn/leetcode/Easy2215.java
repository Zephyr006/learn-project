package learn.leetcode;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Easy2215 {
    public static void main(String[] args) throws InterruptedException {

    }

    public List<List<Integer>> findDifference(int[] nums1, int[] nums2) {
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();
        List<Integer> list1 = new ArrayList();
        List<Integer> list2 = new ArrayList();

        for (int num : nums1) {
            set1.add(num);
        }
        for (int num : nums2) {
            set2.add(num);
        }

        for (int num : set1) {
            if (!set2.contains(num)) {
                list1.add(num);
            }
        }
        for (int num : set2) {
            if (!set1.contains(num)) {
                list2.add(num);
            }
        }

        return new ArrayList(Arrays.asList(list1, list2));
    }
}
