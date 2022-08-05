package person.companion.excel.assist;

import cn.hutool.core.date.DatePattern;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import person.companion.excel.model.ExcelImportR;
import person.companion.excel.model.ImportParam;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title: excel数据导入辅助类
 * Author: companion
 * Written by: 2022/8/3 11:41
 */
public class ExcelImport {
    /**
     * 保存数据
     *
     * @param startRow 开始行
     * @param property 属性
     * @param workbook 文件
     * @param cls      类
     * @param <E>      类
     * @return 返回参数
     * @throws Exception 异常
     */
    public static <E> List<E> insertIntoEntity(int startRow, Map<Integer, String> property, Workbook workbook, Class<E> cls) throws Exception {
        List<E> dataList = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(0);
        if (sheet.getLastRowNum() == 0) {
            throw new Exception("数据为空");
        }

        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            E e = cls.getDeclaredConstructor().newInstance();
            Row row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                // 通过反射将值保存到字段
                Field field = e.getClass().getDeclaredField(property.get(j));
                field.setAccessible(true);
                setValueToFiled(e, field, cell, sheet);
            }

            dataList.add(e);
        }

        return dataList;
    }

    /**
     * 有验证导入
     *
     * @param startRow 开始行
     * @param property 属性
     * @param workbook 文件
     * @param cls      类
     * @param <E>      类
     * @return 返回参数
     * @throws Exception 异常
     */
    public static <E> ExcelImportR<E> insertIntoEntity1(int startRow, Map<Integer, ImportParam<E>> property, XSSFWorkbook workbook, Class<E> cls) throws Exception {
        ExcelImportR<E> result = new ExcelImportR<>();
        List<JsonObject> errList = new ArrayList<>();
        List<E> dataList = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);
        if (sheet.getLastRowNum() == 0) {
            throw new Exception("数据为空");
        }

        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            E e = cls.getDeclaredConstructor().newInstance();
            Row row = sheet.getRow(i);
            boolean flag = true;
            StringBuilder errMsg = new StringBuilder();

            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);
                ImportParam<E> importParam = property.get(j);
                // 先验证数据是否正确
                if (importParam.getValid() != null && !importParam.getValid().valid(e, cell.getStringCellValue(), errMsg)) {
                    flag = false;
                    continue;
                }

                // 如果有自定义数据处理就用自定义的数据处理
                if (importParam.getHandle() != null) {
                    importParam.getHandle().handle(e, cell.getStringCellValue());
                    continue;
                }

                // 通过反射将值保存到字段
                Field field = e.getClass().getDeclaredField(importParam.getProperty());
                field.setAccessible(true);
                setValueToFiled(e, field, cell, sheet);
            }

            if (flag) {
                dataList.add(e);
                continue;
            }

            Gson gson = new GsonBuilder().setDateFormat(DatePattern.NORM_DATE_PATTERN).serializeNulls().create();
            JsonObject jsonObject = gson.toJsonTree(e).getAsJsonObject();
            jsonObject.addProperty("errMsg", errMsg.toString());
            errList.add(jsonObject);
        }

        result.setData(dataList);
        result.setErrorData(errList);
        return result;
    }

    /**
     * 将值保存到字段
     *
     * @param e     类
     * @param field 字段
     * @param cell  单元格
     * @param sheet 表格
     */
    private static <E> void setValueToFiled(E e, Field field, Cell cell, Sheet sheet) throws IllegalAccessException {
        // 判断当前单元格是否合并，如果合并取第一个单元格的值
        CellRangeAddress addresses = sheet.getMergedRegions().stream().filter(c -> c.isInRange(cell)).findFirst().orElse(null);
        if (addresses == null) {
            field.set(e, convertValueType(cell, field.getGenericType().getTypeName()));
            return;
        }

        Cell firstCell = sheet.getRow(addresses.getFirstRow()).getCell(addresses.getFirstColumn());
        field.set(e, convertValueType(firstCell, field.getGenericType().getTypeName()));
    }

    /**
     * 获取对应类型的数据
     *
     * @param cell     单元格
     * @param typeName 字段类型
     * @return 数据
     */
    private static Object convertValueType(Cell cell, String typeName) {
        String value = getCellStringValue(cell);
        if (value == null) {
            return null;
        }

        switch (typeName) {
            case "java.lang.String":
                return value;
            case "java.lang.Short":
                return Short.parseShort(value.split("\\.")[0]);
            case "java.lang.Integer":
                return Integer.parseInt(value.split("\\.")[0]);
            case "java.lang.Long":
                return Long.parseLong(value.split("\\.")[0]);
            case "java.lang.Float":
                return Float.parseFloat(value);
            case "java.lang.Double":
                return Double.parseDouble(value);
            case "java.math.Boolean":
                return Boolean.parseBoolean(value);
            case "java.math.BigDecimal":
                return new BigDecimal(value);
            default:
                return null;
        }
    }

    /**
     * 将单元格的值转换为String类型
     *
     * @param cell 单元格
     * @return 值
     */
    private static String getCellStringValue(Cell cell) {
        switch (cell.getCellType()) {
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    /**
     * 获取合并信息
     *
     * @param sheet    表格
     * @param startRow 数据开始行
     * @return 合并信息
     */
    private static String getMergeInfo(Sheet sheet, int startRow) {
        List<HashMap<String, Integer>> maps = new ArrayList<>();
        for (CellRangeAddress region : sheet.getMergedRegions()) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("_firstRow", region.getFirstRow() - startRow);
            map.put("_lastRow", region.getLastRow() - startRow);
            map.put("_firstCol", region.getFirstColumn());
            map.put("_lastCol", region.getLastColumn());

            maps.add(map);
        }

        return new Gson().toJson(maps);
    }
}
