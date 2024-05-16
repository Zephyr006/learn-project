package learn.leetcode;

public class Medium2105 {

    public static void main(String[] args) {
        int[] plants = LeetcodeHelper.toIntArray("[726,739,934,116,643,648,473,984,482,85,850,806,146,764,156,66,186,339,985,237,662,552,800,78,617,933,481,652,796,594,151,82,183,241,525,221,951,732,799,483,368,354,776,175,974,187,913,842]");
        int result = new Medium2105().minimumRefill(plants, 1439, 1207);
        System.out.println(result);
    }

    public int minimumRefill(int[] plants, int capacityA, int capacityB) {
        // 灌满水的次数
        int count = 0;
        // 双指针 模拟浇水
        int left = 0, right = plants.length - 1;
        // 记录 A、B 的最大容量
        int maxA=capacityA, maxB = capacityB;
        while (left < right) {
            // 给花浇水，如果剩余水量为负数，说明需要灌水，count+1
            if (capacityA < plants[left]) {
                count++;
                capacityA = maxA;
            }
            capacityA -= plants[left];
            left++;

            // B 容器也同理
            if (capacityB < plants[right]) {
                count++;
                capacityB = maxB;
            }
            capacityB -= plants[right];
            right--;
        }

        // ！遍历处理完之后，左右指针可能到达在相同位置，当前水罐中水 更多 的人会给这株植物浇水
        if (left == right) {
            if (capacityA > capacityB || capacityA == capacityB) {
                if (capacityA < plants[left]) {
                    count++;
                }
            } else {
                if (capacityB < plants[right]) {
                    count++;
                }
            }
        }

        return count;
    }

}
