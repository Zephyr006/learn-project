package learn.leetcode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.util.Arrays;
import java.util.List;

/**
 * @author Zephyr
 * @since 2022-04-23.
 */
public class LeetcodeHelper {

    /**
     * 字符串转二维数组，字符串示例如下
     * <pre> {@code
     *     [
     *   [1,3,1],
     *   [1,5,1],
     *   [4,2,1]
     * ]
     * }</pre>
     */
    public static int[][] parse2DIntArray(String str) {
        List<Integer[]> arrayList = JSONArray
            .parseArray(str.replace("\n", ""), Integer[].class);
        int[][] result = new int[arrayList.size()][arrayList.get(0).length];
        for (int i = 0; i < arrayList.size(); i++) {
            // Integer数组转为int数组
            result[i] = Arrays.stream(arrayList.get(i)).mapToInt(Integer::valueOf).toArray();
        }
        return result;
    }

    public static char[][] parse2DCharArray(String str) {
        if (str == null || str.length() <= 2) {
            return new char[0][0];
        }
        JSONArray jsonArray = JSON.parseArray(str);

        char[][] charArray = new char[jsonArray.size()][];
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray innerArray = jsonArray.getJSONArray(i);
            char[] innerCharArray = new char[innerArray.size()];
            for (int j = 0; j < innerArray.size(); j++) {
                innerCharArray[j] = innerArray.getString(j).charAt(0);
            }
            charArray[i] = innerCharArray;
        }
        return charArray;
    }

    public static int[] toIntArray(String s) {
        if (s == null || s.length() <= 2) {
            return new int[0];
        }
        s = s.substring(1, s.length() - 1);
        try {
            return Arrays.stream(s.split(",")).mapToInt(Integer::parseInt).toArray();
        } catch (Exception e) {
            return Arrays.stream(s.split(", ")).mapToInt(Integer::parseInt).toArray();
        }
    }
}
