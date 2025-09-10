package learn.leetcode;

import java.util.LinkedList;

/**
 * 735. 小行星碰撞
 *
 * https://leetcode.cn/problems/asteroid-collision/description/?envType=study-plan-v2&envId=leetcode-75
 */
public class Medium735 {
    public static void main(String[] args) {
        int[] ints = new Medium735().asteroidCollision(LeetcodeHelper.toIntArray("1,-2,3,-4"));
        LeetcodeHelper.print(ints);
        ints = new Medium735().asteroidCollision(LeetcodeHelper.toIntArray("-2,-1,1,2"));
        LeetcodeHelper.print(ints);
        ints = new Medium735().asteroidCollision(LeetcodeHelper.toIntArray("1,-2,-2,-2"));
        LeetcodeHelper.print(ints);
    }


    /**
     * 栈：模拟栈记录未爆炸的星星，遍历数据，判断每个元素是否会和之前未爆炸的星星发生爆炸
     */
    public int[] asteroidCollision(int[] asteroids) {
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < asteroids.length; i++) {
            if (asteroids[i] > 0 || list.isEmpty()) {
                list.addLast(asteroids[i]);
            // steroids[i] != 0
            } else {
                // 发现向左移动的小星星，判断碰撞情况：
                // 如果向右移动的小星星体积（绝对值）更大，则向左移动的小星星爆炸，忽略当前值
                if (list.getLast() > -asteroids[i]) {
                    continue;
                // 如果相邻星星大小一样，方向相反，直接都爆炸
                } else if (list.getLast() == -asteroids[i]) {
                    list.removeLast();
                } else {
                    // 向左移动的小星星更大，持续向左碰撞，直到左侧没有小星星，或者都是向左的星星
                    boolean containsRight = true;
                    while (!list.isEmpty()) {
                        if (list.getLast() == -asteroids[i]) {
                            list.removeLast();
                            containsRight = false;
                            break;
                        } else if (list.getLast() < -asteroids[i]) {
                            containsRight = true;
                            // 只剩下向左的小星星（负数）
                            if (list.getLast() < 0) {
                                break;
                            } else {
                                list.removeLast();
                            }
                        // 栈中的星星大于当前星星，当前星星爆炸
                        } else {
                            containsRight = false;
                            break;
                        }
                    }
                    if (containsRight) {
                        list.addLast(asteroids[i]);
                    }
                }
            }
        }

        return list.stream().mapToInt(i -> i).toArray();
    }
}
