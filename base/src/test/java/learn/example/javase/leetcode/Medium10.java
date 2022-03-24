package learn.example.javase.leetcode;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 599. 两个列表的最小索引总和
 * https://leetcode-cn.com/problems/minimum-index-sum-of-two-lists/
 *
 * @author Zephyr
 * @date 2022/3/14.
 */
public class Medium10 {

    public static void main(String[] args) {
        List<String> list1 = JSONArray.parseArray("[\"Shogun\",\"Tapioca Express\",\"Burger King\",\"KFC\"]", String.class);
        List<String> list2 = JSONArray.parseArray("[\"KFC\",\"Shogun\",\"Burger King\"]", String.class);
        String[] restaurant = new Medium10().findRestaurant(list1.toArray(new String[0]), list2.toArray(new String[0]));
        System.out.println(restaurant);
    }

    public String[] findRestaurant(String[] list1, String[] list2) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < list1.length; i++) {
            map.put(list1[i], i);
        }

        List<String> res = new ArrayList<>();
        // 定义一个变量保存最小索引值，这个值得初始值使用一个较大值，以保证第一次比较时任意索引值都比它小
        int smallIdx = 99999;
        for (int i = 0; i < list2.length; i++) {
            // 优化：如果当前索引值比最小索引和还大，那么后面的元素值一定不能找到小于当前最小索引和的元素，可以提前结束循环
            if (i > smallIdx) {
                break;
            }
            Integer idxIn = map.get(list2[i]);
            if (idxIn != null) {
                if (idxIn + i < smallIdx) {
                    res.clear();
                    res.add(list2[i]);
                    smallIdx = idxIn + i;
                } else if (idxIn + i == smallIdx) {
                    res.add(list2[i]);
                }
            }
        }
        return res.toArray(new String[0]);
    }


}
