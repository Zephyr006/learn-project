package learn.base.utils;

import org.apache.commons.collections4.CollectionUtils;
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

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

/**
 * @author Zephyr
 * @date 2021/4/9.
 */
public class ExcelUtil {

    /**
     * ！ titles的排列顺序必须与getMethods要调用的方法列表顺序一致 ！
     */
    public static <E> void doExport(List<E> dataList, List<String> titles, List<Function<E, ?>> getMethods, OutputStream os) {
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
                    if (dataValue instanceof Double) {
                        cell.setCellValue((Double) dataValue);
                    } else if (dataValue instanceof Date) {
                        cell.setCellValue((Date) dataValue);
                    } else if (dataValue instanceof LocalDateTime) {
                        cell.setCellValue((LocalDateTime) dataValue);
                    } else if (dataValue instanceof LocalDate) {
                        cell.setCellValue((LocalDate) dataValue);
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

}
