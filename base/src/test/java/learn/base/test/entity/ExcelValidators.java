package learn.base.test.entity;

import learn.base.utils.LambdaExceptionUtil;
import learn.base.utils.Reflections;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2021/4/17.
 */
@Getter
public enum ExcelValidators {

    Test (true, row -> {
        String docIdsCellValue = row.getCell(3).getStringCellValue();
        String optionsCellValue = row.getCell(4).getStringCellValue();
        if ((StringUtils.isBlank(docIdsCellValue) && !StringUtils.isBlank(optionsCellValue))
                || (!StringUtils.isBlank(docIdsCellValue) && StringUtils.isBlank(optionsCellValue))) {
            return new ExcelError(ExcelError.MSG_COMMON_ERROR,
                    row.getCell(3).getAddress().formatAsString(),
                    row.getCell(4).getAddress().formatAsString());
        }

        if (!Patterns.NUMBERS_STRING_PATTERN.matcher(docIdsCellValue.replaceAll("，", ",")).matches()) {
            return new ExcelError(ExcelError.MSG_COMMON_ERROR, row.getCell(3).getAddress().formatAsString());
        }
        if (!Patterns.NUMBERS_STRING_PATTERN.matcher(optionsCellValue.replace("，", ",")).matches()) {
            return new ExcelError(ExcelError.MSG_COMMON_ERROR, row.getCell(4).getAddress().formatAsString());
        }

        return null;
    }),

    Test2 (true, row -> {
        return null;
    });


    private static class Patterns {
        // ^\d+(,\d+)*$或者^(\d+,)*\d+$
        static final Pattern NUMBERS_STRING_PATTERN = Pattern.compile("^(\\d+,)*\\d+$");
    }

    private boolean using;
    private Function<Row, ExcelError> validator;
    ExcelValidators(boolean using, Function<Row, ExcelError> validFunction) {
        this.using = using;
        this.validator = validFunction;
    }

    //public abstract ExcelError valid(Row row);

    public static List<ExcelValidators> getAll() {
        return Arrays.stream(ExcelValidators.values())
                .filter(ExcelValidators::isUsing)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws ReflectiveOperationException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        for (int i = 1; i < 5; i++) {
            Row fillRow = sheet.createRow(i);
            Cell rowCell = fillRow.createCell(0);
            fillRow.createCell(1);
            fillRow.createCell(2);
            fillRow.createCell(3);
            fillRow.createCell(4);
            if (i == 3) {
                rowCell.setCellValue("    ");
            }
        }

        int lastRowNum = sheet.getLastRowNum();
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();

        row.createCell(0);
        row.createCell(1);
        row.createCell(2);
        Cell cell3 = row.createCell(3);
        Cell cell4 = row.createCell(4);
        cell3.setCellValue("123,222，");
        cell4.setCellValue("123，45");

        // == 验证表头
        Set<Integer> notBlankRowNums = new HashSet<>();
        // valid
        List<ExcelValidator> validators = Reflections.getAllAssignedClass(ExcelValidator.class).stream()
                .map(LambdaExceptionUtil.rethrowFunction(Class::newInstance))
                .collect(Collectors.toList());
        for (int rowNum = 0; rowNum < sheet.getLastRowNum(); rowNum++) {  //跳过首行的标题
            Row currentRow = sheet.getRow(rowNum);
            // filter blank row
            boolean blankRow;
            Iterator<Cell> cellIterator = currentRow.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                blankRow = CellType.BLANK.equals(cell.getCellType());
                if (!blankRow && StringUtils.isNotBlank(cell.getStringCellValue()) && notBlankRowNums.add(rowNum)) {
                    break;
                }
            }
        }

        System.out.println("需要验证数据填写争取性的数据行数 = " + notBlankRowNums.size());
        List<ExcelError> excelErrorList = new ArrayList<>();
        for (Integer notBlankRowNum : notBlankRowNums) {
            Row row1 = sheet.getRow(notBlankRowNum);
            List<ExcelError> rowErrors = validators.stream()
                    .map(validator -> validator.validate(row1))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if (!rowErrors.isEmpty()) {
                excelErrorList.addAll(rowErrors);
            }
        }
        if (!excelErrorList.isEmpty()) {
            System.err.println("excel有错误，返回");
        }

        //System.out.println(sheet.getLastRowNum());
        //System.out.println(sheet.getPhysicalNumberOfRows());
        // 使用set标记空白行



        excelErrorList.forEach(System.err::println);
    }

}
