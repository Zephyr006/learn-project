package learn.leetcode;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 2244 完成所有任务需要的最少轮数
 * { @link https://leetcode.cn/problems/minimum-rounds-to-complete-all-tasks/description/}
 * @date 2024-05-14 19:54:09
 */
public class Medium2244 {

    public static void main(String[] args) {
        int[] param = LeetcodeHelper.toIntArray("[5,5,5,5]");
        int result = new Medium2244().minimumRounds(param);
        System.out.println(result);
        Arrays.stream(new int[0]).boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    public int minimumRounds(int[] tasks) {
        // 贪心算法：首先统计不同难度级别的任务各自出现的频率，然后对频率（≥1\ge 1≥1）进行分类
        Map<Integer, Long> keyToCountMap = Arrays.stream(tasks).boxed().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        int res = 0;
        for (Long count : keyToCountMap.values()) {
            // 如果只出现了一次，无法完成
            if (count == 1) {
                return -1;
            }
            // 如果能被 3 整除，则完成任务需要 count / 3 轮
            if (count % 3 == 0) {
                res+= count / 3;
            }
            //如果被 3 除后余 1 或 2，则最后一轮/两轮每次完成 2 个任务
            if (count % 3 == 1) {
                res += (1 + count/3);
            }
            if (count % 3 == 2) {
                res += (1 + count/3);
            }
        }
        return res;
    }
}
