package learn.base.utils;

import java.util.List;

/**
 * 逻辑分页工具类
 * @author Zephyr
 * @date 2021/3/18.
 */
public class LogicalPageUtil {

    /**
     * @param page 页数，从 1 开始
     * @param limit 每页记录数，不能小于 1； 如果需要不分页，则 limit 传 Long.MAX_VALUE即可
     */
    public static <E> List<E> page(List<E> recordList, Long page, Long limit) {
        if (page == null || limit == null || recordList == null || page < 1 || limit < 1) {
            return recordList;
        }

        int toSize = (int) (page * limit);
        // 总记录数不足一页，直接返回所有记录
        if (page.compareTo(1L) == 0 && recordList.size() <= toSize) {
            return recordList;
        }
        return recordList.size() <= toSize
                ? recordList.subList(toSize - limit.intValue(), recordList.size())
                : recordList.subList(toSize - limit.intValue(), toSize);
    }

}

