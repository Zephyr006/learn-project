package learn.base.utils;

import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
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
     * 导出Excel模板，传入指定的表头
     * @param headers 表头的文字，按顺序填充
     * @param needLock 是否需要锁定表头以防止表头被修改，如果需要锁定，则必须传入有效的 filledLines 值，
     * @param filledLines 如果 needLock == true，则必须传入有效的 filledLines 值（ >0 ），否则 filledLines 以外的行将被锁定不能被编辑
     */
    public static byte[] genExportTemplate(final List<String> headers, final String sheetName, final boolean needLock, final int filledLines) throws IOException {
        if (CollectionUtils.isEmpty(headers)) {
            throw new IllegalArgumentException("Excel导出模板中的表头不能为空！");
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(sheetName == null ? "Sheet" : sheetName);
        sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth() * 4);
        if (needLock) {
            sheet.protectSheet("87654321");
            sheet.lockSort(false);
            sheet.lockInsertRows(false);
            sheet.lockDeleteRows(false);
            sheet.lockFormatRows(false);
            sheet.lockFormatColumns(false);
            sheet.lockFormatCells(false);
            sheet.lockObjects(false);
        }

        CellStyle headerCellStyle = ExcelUtil.createCellStyle(workbook, true, needLock);
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellStyle(headerCellStyle);
            headerCell.setCellValue(headers.get(i));
            sheet.setColumnWidth(i, headers.get(i).getBytes().length * 256 + 1024);
        }

        if (needLock) {
            if (filledLines < 1) {
                throw new IllegalAccessError("导出锁定类型的Excel模板时，'filledLines'值必须大于0，否则无法编辑Excel中的内容");
            }
            CellStyle unLockedCellStyle = ExcelUtil.createCellStyle(workbook, false, false);
            for (int i = 1; i < filledLines; i++) {
                Row filledRow = sheet.createRow(i);
                // 解锁可编辑内容的单元格
                for (int cellIndex = 0; cellIndex < headers.size(); cellIndex++) {
                    filledRow.createCell(cellIndex).setCellStyle(unLockedCellStyle);
                }
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 用于导出每一行数据格式都一致的数据
     * @apiNote headers的排列顺序必须与getMethods要调用的方法列表顺序一致 ！
     * @apiNote 已支持的格式化类型：Date、LocalDate、LocalDateTime、数字类型，布尔值会显示为'TRUE、FALSE'，其他类型默认调用toString()
     *
     * @param dataList   要导出的数据
     * @param headers    导出数据的表头（如果为空，则不包含表头；否则认为 headers 中的所有表头为一行）
     * @param getMethods 针对每条数据，要调用的getter方法声明，顺序必须与headers的顺序一一对应
     * @param os         excel数据输出流
     */
    public static <E> void exportCommonData(List<E> dataList, List<String> headers, List<Function<E, ?>> getMethods, OutputStream os) throws IOException {
        if (CollectionUtils.isEmpty(dataList)) {
            System.err.println("导出Excel错误：数据为空！");
            return;
        }
        try (Workbook workbook = new SXSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet");
            sheet.setDefaultColumnWidth(sheet.getDefaultColumnWidth() * 2);
            //short dateFormat = workbook.createDataFormat().getFormat("yyyy-MM-dd");
            //short dateTimeFormat = workbook.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss");

            // fill header
            int headerRowCount = 0;
            if (CollectionUtils.isNotEmpty(headers)) {
                headerRowCount = 1;
                Row headerRow = sheet.createRow(0);
                headerRow.setHeight((short) (headerRow.getHeight() * 2));
                CellStyle headerStyle = createCellStyle(workbook, true, null);
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    String headerValue = headers.get(i);
                    cell.setCellValue(headerValue);
                    cell.setCellStyle(headerStyle);
                    sheet.setColumnWidth(i, headerValue.getBytes(StandardCharsets.UTF_8).length * 256 + 1024);
                }
            }

            // init CellStyle
            CellStyle contentCellStyle = createCellStyle(workbook, false, BuiltinFormats.getBuiltinFormat(0));
            CellStyle dateCellStyle = createCellStyle(workbook, false, "yyyy-MM-dd");
            CellStyle dateTimeCellStyle = createCellStyle(workbook, false, "yyyy-MM-dd HH:mm:ss");
            // fill content
            int skipCount = 0;
            for (int i = 0; i < dataList.size(); i++) {
                E data = dataList.get(i);
                if (data == null) {
                    skipCount++;
                    continue;
                }
                Row row = sheet.createRow(i + headerRowCount - skipCount);
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

    private static CellStyle createCellStyle(final Workbook workbook, boolean isHeader) {
        return createCellStyle(workbook, isHeader, false, null);
    }
    private static CellStyle createCellStyle(final Workbook workbook, boolean isHeader, boolean locked) {
        return createCellStyle(workbook, isHeader, locked, null);
    }
    private static CellStyle createCellStyle(final Workbook workbook, boolean isHeader, String timeFormat) {
        return createCellStyle(workbook, isHeader, false, timeFormat);
    }
    private static CellStyle createCellStyle(final Workbook workbook, boolean isHeader, boolean locked, String timeFormat) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        // 设置字体名字
        //font.setFontName("Courier New");
        if (isHeader) {
            // 设置字体大小
            font.setFontHeightInPoints((short) 16);
            // 单元格背景颜色
            style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
            // 设置水平对齐和垂直对齐的样式
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.BOTTOM);
        } else {
            font.setFontHeightInPoints((short) 14);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setWrapText(false);
        }
        // 设置数据格式化方式，具体样式参考Excel文件中的单元格格式设置界面，默认提供的格式在 {@link BuiltinFormats}.
        if (timeFormat != null && !timeFormat.isEmpty()) {
            style.setDataFormat(workbook.createDataFormat().getFormat(timeFormat));
        }
        // 在样式中应用设置的字体
        style.setFont(font);
        style.setLocked(locked);
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
                ExcelUtil.exportCommonData(Collections.singletonList(new Test()),
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
