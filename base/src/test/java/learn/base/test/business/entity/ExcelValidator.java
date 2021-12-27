package learn.base.test.business.entity;

import org.apache.poi.ss.usermodel.Row;

import java.util.regex.Pattern;

/**
 *
 * @author Zephyr
 * @date 2021/4/17.
 */
public interface ExcelValidator {
    // 正则 - 逗号分隔的数字：^\d+(,\d+)*$或者^(\d+,)*\d+$
    static final Pattern NUMBERS_STRING_PATTERN = Pattern.compile("^(\\d+,)*\\d+$");

    /**
     * 验证excel中当前行是否满足指定条件，不满足则返回相应的 ExcelError
     *
     * @apiNote 如果不存在相应错误，则返回null，注意过滤返回结果
     * @implNote 所有实现类必须与接口定义类在同一包路径下！
     * @return 如果存在错误，返回对应的错误 ExcelError，否则返回 null
     */
    ExcelError validate(Row row);

}
