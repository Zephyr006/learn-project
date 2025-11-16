package learn.leetcode;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Medium347 {
    public static void main(String[] args) {
        int[] res = new Medium347().topKFrequent(LeetcodeHelper.toIntArray("[4,1,-1,2,-1,2,3]"), 2);
        LeetcodeHelper.print(res);
    }

    public int[] topKFrequent(int[] nums, int k) {
    //     使用 map 记录每个元素出现的次数
        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<Map.Entry<Integer, Integer>> queue = new PriorityQueue<>(k,
                (o1, o2) -> o1.getValue() - o2.getValue());
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (queue.size() < k) {
                queue.add(entry);
            } else {
                if (queue.peek().getValue() < entry.getValue()) {
                    queue.poll();
                    queue.add(entry);
                }
            }
        }

        int index = 0;
        int[] res = new int[k];
        while (!queue.isEmpty()) {
            res[index++] = queue.poll().getKey();
        }
        return res;
    }
}
