package person.companion.excel.assist;

import cn.hutool.core.io.resource.ClassPathResource;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import person.companion.excel.model.ColumnParam;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.apache.poi.hssf.record.DefaultColWidthRecord.DEFAULT_COLUMN_WIDTH;

/**
 * Title: excel数据导出辅助类
 * Author: companion
 * Written by: 2022/8/3 14:37
 */
public class ExcelExport {
    /**
     * 创建数据行
     *
     * @param sheet     表
     * @param fieldList 参数
     * @param dataList  数据
     */
    public static void createDataRow(Sheet sheet, List<String> fieldList, JsonArray dataList) {
        int firstIndex = 1;
        for (int rowIndex = 0; rowIndex < dataList.size(); rowIndex++) {
            Row row = sheet.createRow(firstIndex + rowIndex);
            JsonObject object = dataList.get(rowIndex).getAsJsonObject();
            for (int colIndex = 0; colIndex < fieldList.size(); colIndex++) {
                Cell cell = row.createCell(colIndex);
                JsonElement element = object.get(fieldList.get(colIndex));

                cell.setCellValue(element == null ? "" : element.getAsString());
            }
        }
    }

    /**
     * 创建标题行
     *
     * @param sheet   表格
     * @param style   样式
     * @param columns 参数
     */
    public static void createTitleRow(Sheet sheet, List<ColumnParam> columns, CellStyle style) {
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);

        Row row = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            ColumnParam column = columns.get(i);

            int width = column.getWidth() == null ? DEFAULT_COLUMN_WIDTH : column.getWidth() * 256;
            sheet.setColumnWidth(i, width);
            Cell cell = row.createCell(column.getFirstCol());
            cell.setCellStyle(style);
            cell.setCellValue(column.getTitle());

            if (!column.getFirstCol().equals(column.getLastCol()) || !column.getFirstRow().equals(column.getLastRow())) {
                CellRangeAddress rangeAddress = new CellRangeAddress(column.getFirstRow(), column.getLastRow(), column.getFirstCol(), column.getLastCol());
                sheet.addMergedRegion(rangeAddress);
            }
        }
    }

    /**
     * 根据文件路径复制文件并返回复制后的文件
     *
     * @param path 文件相对路径
     * @return 复制后的文件
     */
    public static File copyFile(String path) throws Exception {
        // 从模板文件复制一份临时文件
        File dest = new File(UUID.randomUUID().toString());

        // if (inputStream == null) {
        //     // windows获取文件方式
        //     inputStream = new FileInputStream(ResourceUtils.getFile("classpath:" + path));
        // }

        // 复制文件
        try (InputStream inputStream = new ClassPathResource(path).getStream(); OutputStream os = new FileOutputStream(dest)) {
            int len = 0;
            byte[] buffer = new byte[8192];

            while ((len = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }

        return dest;
    }

    /**
     * 复制单元格样式
     *
     * @param source   源
     * @param dest     目标
     * @param styleMap 样式
     */
    private static void copyCellStyle(Cell source, Cell dest, Map<Integer, CellStyle> styleMap) {
        int stHashCode = source.getCellStyle().hashCode();
        CellStyle newCellStyle = styleMap.get(stHashCode);
        if (newCellStyle == null) {
            newCellStyle = dest.getSheet().getWorkbook().createCellStyle();
            newCellStyle.cloneStyleFrom(source.getCellStyle());
            styleMap.put(stHashCode, newCellStyle);
        }

        dest.setCellStyle(newCellStyle);
    }

    /**
     * 将当前sheet的数据复制到新的workbook
     *
     * @param workbook 新的workbook
     * @param source   sheet
     * @return 复制后sheet
     */
    public static Sheet copySheet(Workbook workbook, Sheet source) {
        // 用于保存单元格格式
        Map<Integer, CellStyle> styleMap = new HashMap<>();
        Sheet target = workbook.createSheet(source.getSheetName());
        // 数据和格式
        for (int i = 0; i <= source.getLastRowNum(); i++) {
            Row sourceRow = source.getRow(i);
            Row targetRow = target.createRow(i);
            // 高度
            targetRow.setHeight(sourceRow.getHeight());
            for (int i1 = 0; i1 < sourceRow.getLastCellNum(); i1++) {
                Cell sourceCell = sourceRow.getCell(i1);
                Cell targetCell = targetRow.createCell(i1);

                targetCell.setCellValue(sourceCell.getStringCellValue());
                // 宽度
                target.setColumnWidth(i1, source.getColumnWidth(i1));
                copyCellStyle(sourceCell, targetCell, styleMap);
            }
        }

        // 合并信息
        source.getMergedRegions().forEach(target::addMergedRegion);
        return target;
    }
}
