package learn.base.test.business.entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

/**
 * @author Zephyr
 * @since 2021-4-17.
 */
public class ExcelValidatorExample implements ExcelValidator {

    @Override
    public ExcelError validate(Row row) {
        String docIdsCellValue = row.getCell(3).getStringCellValue();
        String optionsCellValue = row.getCell(4).getStringCellValue();
        if ((StringUtils.isBlank(docIdsCellValue) && !StringUtils.isBlank(optionsCellValue))
                || (!StringUtils.isBlank(docIdsCellValue) && StringUtils.isBlank(optionsCellValue))) {
            return new ExcelError(ExcelError.MSG_COMMON_ERROR,
                    row.getCell(3).getAddress().formatAsString(),
                    row.getCell(4).getAddress().formatAsString());
        }

        if (!NUMBERS_STRING_PATTERN.matcher(docIdsCellValue.replaceAll("，", ",")).matches()) {
            return new ExcelError(ExcelError.MSG_COMMON_ERROR, row.getCell(3).getAddress().formatAsString());
        }
        if (!NUMBERS_STRING_PATTERN.matcher(optionsCellValue.replace("，", ",")).matches()) {
            return new ExcelError(ExcelError.MSG_COMMON_ERROR, row.getCell(4).getAddress().formatAsString());
        }

        return null;
    }
}
