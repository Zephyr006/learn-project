package learn.base.utils;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * @author Zephyr
 * @date 2021/4/9.
 */
public class ExcelUtil {

    /**
     * @apiNote titles的排列顺序必须与getMethods要调用的方法列表顺序一致 ！
     */
    public static <E> void export(List<E> dataList, List<String> titles, List<Function<E, ?>> getMethods, OutputStream os) {
        if (CollectionUtils.isEmpty(dataList)) {
            System.err.println("导出Excel：数据为空");
            return;
        }
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("default sheet");
            sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth() * 2);

            // fill title
            int titleRowCount = 0;
            if (CollectionUtils.isNotEmpty(titles)) {
                titleRowCount = 1;
                Row titleRow = sheet.createRow(0);
                titleRow.setHeight((short) (titleRow.getHeight() * 2));
                for (int i = 0; i < titles.size(); i++) {
                    Cell cell = titleRow.createCell(i);
                    cell.setCellValue(titles.get(i));
                    cell.setCellStyle(getCellStyle(workbook, true));
                }
            }

            // fill content
            int skipCount = 0;
            CellStyle contentCellStyle = getCellStyle(workbook, false);
            for (int i = 0; i < dataList.size(); i++) {
                E data = dataList.get(i);
                if (data == null) {
                    skipCount++;
                    continue;
                }
                Row row = sheet.createRow(i + titleRowCount - skipCount);
                for (int j = 0; j < getMethods.size(); j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellStyle(contentCellStyle);
                    Object dataValue = getMethods.get(j).apply(data);
                    if (dataValue instanceof Double || dataValue instanceof Long || dataValue instanceof Integer
                            || dataValue instanceof Short || dataValue instanceof BigDecimal || dataValue instanceof Float) {
                        cell.setCellValue(((Number) dataValue).doubleValue());
                    //} else if (dataValue instanceof Date) {
                    //    cell.setCellValue((Date) dataValue);
                    //} else if (dataValue instanceof LocalDateTime) {
                    //    cell.setCellValue((LocalDateTime) dataValue);
                    //} else if (dataValue instanceof LocalDate) {  //LocalTime not support
                    //    cell.setCellValue((LocalDate) dataValue);
                    } else if (dataValue instanceof Boolean) {
                        cell.setCellValue((Boolean) dataValue);
                    } else if (dataValue instanceof RichTextString) {
                        cell.setCellValue((RichTextString) dataValue);
                    } else {
                        cell.setCellValue(dataValue.toString());
                    }
                }
            }

            workbook.write(os);
            System.out.println("Excel导出完成。");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static CellStyle getCellStyle(final Workbook workbook, boolean isTitle) {
        CellStyle style = workbook.createCellStyle();
        // 设置字体
        Font font = workbook.createFont();
        // 设置字体名字
        //font.setFontName("Courier New");
        if (isTitle) {
            // 设置字体大小
            font.setFontHeightInPoints((short) 16);
            // 字体加粗
            //font.setBold(true);
            // 单元格背景颜色
            style.setFillBackgroundColor(IndexedColors.LAVENDER.getIndex());
            style.setAlignment(HorizontalAlignment.CENTER);
        } else {
            font.setFontHeightInPoints((short) 14);
        }
        // 在样式中应用设置的字体
        style.setFont(font);
        // 设置水平对齐和垂直对齐的样式
        style.setAlignment(HorizontalAlignment.GENERAL);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }



    /*
     * 时间类型的字段会被转换成double类型值显示
     */
    @Getter
    private static class Test {
        Short aShort = (short) 233;
        BigDecimal bigDecimal = new BigDecimal("1.2233");
        Double aDouble = 233.2d;
        Float aFloat = 1.112f; //Float类型可能因精度不准确导致显示异常
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = new Date();
        Boolean aBoolean = false;  // 大写的TRUE、FALSE

        // XXX 时间类型值会显示为double类型的格式，不如直接调用toString()可读性好，待改进，数值类型显示正常
        public static void main(String[] args) throws FileNotFoundException {
            String exportFilePath = "/Users/wang/Desktop/TestExcel.xlsx";
            ExcelUtil.export(Collections.singletonList(new Test()),
                    Arrays.asList("Short", "BigDecimal", "Double", "Float", "LocalDate", "LocalTime", "LocalDateTime", "Date", "Boolean"),
                    Arrays.asList(Test::getAShort, Test::getBigDecimal, Test::getADouble, Test::getAFloat,
                            Test::getLocalDate, Test::getLocalTime, Test::getLocalDateTime, Test::getDate, Test::getABoolean),
                    new FileOutputStream(new File(exportFilePath)));
        }
    }

}
