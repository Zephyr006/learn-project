package learn.base.utils;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
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
     * @apiNote 已支持的格式化类型：Date、LocalDate、LocalDateTime、数字类型，布尔值会显示为'TRUE、FALSE'，其他类型默认调用toString()
     */
    public static <E> void export(List<E> dataList, List<String> titles, List<Function<E, ?>> getMethods, OutputStream os) throws IOException {
        if (CollectionUtils.isEmpty(dataList)) {
            System.err.println("导出Excel错误：数据为空！");
            return;
        }
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("default sheet");
            sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth() * 2);
            //short dateFormat = workbook.createDataFormat().getFormat("yyyy-MM-dd");
            //short dateTimeFormat = workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss");

            // fill title
            int titleRowCount = 0;
            if (CollectionUtils.isNotEmpty(titles)) {
                titleRowCount = 1;
                Row titleRow = sheet.createRow(0);
                titleRow.setHeight((short) (titleRow.getHeight() * 2));
                for (int i = 0; i < titles.size(); i++) {
                    Cell cell = titleRow.createCell(i);
                    cell.setCellValue(titles.get(i));
                    cell.setCellStyle(getCellStyle(workbook, true, null));
                }
            }

            // init CellStyle
            CellStyle contentCellStyle = getCellStyle(workbook, false, BuiltinFormats.getBuiltinFormat(0));
            CellStyle dateCellStyle = getCellStyle(workbook, false, "yyyy-MM-dd");
            CellStyle dateTimeCellStyle = getCellStyle(workbook, false, "yyyy-MM-dd HH:mm:ss");
            // fill content
            int skipCount = 0;
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
                    } else if (dataValue instanceof Date) {
                        cell.setCellValue((Date) dataValue);
                        cell.setCellStyle(dateTimeCellStyle);
                    } else if (dataValue instanceof LocalDateTime) {
                        cell.setCellValue((LocalDateTime) dataValue);
                        cell.setCellStyle(dateTimeCellStyle);
                    } else if (dataValue instanceof LocalDate) {  //LocalTime not support
                        cell.setCellValue((LocalDate) dataValue);
                        cell.setCellStyle(dateCellStyle);
                    } else if (dataValue instanceof Boolean) {
                        cell.setCellValue((Boolean) dataValue);
                    } else if (dataValue instanceof RichTextString) {
                        cell.setCellValue((RichTextString) dataValue);
                    } else {
                        cell.setCellValue(dataValue == null ? "" : dataValue.toString());
                    }
                }
            }

            workbook.write(os);
            System.out.println("Excel导出成功。");
        }
    }

    private static CellStyle getCellStyle(final Workbook workbook, boolean isTitle, String timeFormat) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        // 设置字体名字
        //font.setFontName("Courier New");
        if (isTitle) {
            // 设置字体大小
            font.setFontHeightInPoints((short) 16);
            // 单元格背景颜色
            style.setFillBackgroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            // 设置水平对齐和垂直对齐的样式
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        } else {
            font.setFontHeightInPoints((short) 14);
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        // 设置数据格式化方式，具体样式参考Excel文件中的单元格格式设置界面，默认提供的格式在 {@link BuiltinFormats}.
        if (timeFormat != null && !timeFormat.isEmpty()) {
            style.setDataFormat(workbook.createDataFormat().getFormat(timeFormat));
        }
        // 在样式中应用设置的字体
        style.setFont(font);
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
        String string = "普通文本";

        // XXX 时间类型值会显示为double类型的格式，不如直接调用toString()可读性好，待改进，数值类型显示正常
        public static void main(String[] args) {
            String exportFilePath = "/Users/wang/Desktop/TestExcel.xlsx";

            try (FileOutputStream outputStream = new FileOutputStream(new File(exportFilePath))) {
                ExcelUtil.export(Collections.singletonList(new Test()),
                        Arrays.asList("Short", "BigDecimal", "Double", "Float", "LocalDate", "LocalTime", "LocalDateTime", "Date", "Boolean", "String"),
                        Arrays.asList(Test::getAShort, Test::getBigDecimal, Test::getADouble, Test::getAFloat,
                                Test::getLocalDate, Test::getLocalTime, Test::getLocalDateTime, Test::getDate, Test::getABoolean, Test::getString),
                        outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
