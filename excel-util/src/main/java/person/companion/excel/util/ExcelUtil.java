package person.companion.excel.util;

import person.companion.excel.assist.ExcelImport;
import person.companion.excel.model.ExcelImportR;
import person.companion.excel.model.ImportParam;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Title:
 * Author companion
 * Written by: 2022/1/6 15:12
 * Describe:
 */
public class ExcelUtil {
    /**
     * 导入数据
     *
     * @param startRow 开始行
     * @param property 属性
     * @param is       文件
     * @param cls      类
     * @param <E>      类
     * @return 返回参数
     * @throws Exception 异常
     */
    public static <E> List<E> parseExcel(int startRow, Map<Integer, String> property, InputStream is, Class<E> cls) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            return ExcelImport.insertIntoEntity(startRow, property, workbook, cls);
        }
    }

    /**
     * 有验证的导入数据
     *
     * @param startRow 开始行
     * @param property 属性
     * @param is       文件
     * @param cls      类
     * @param <E>      类
     * @return 返回参数
     * @throws Exception 异常
     */
    public static <E> ExcelImportR<E> parseExcel1(int startRow, Map<Integer, ImportParam<E>> property, InputStream is, Class<E> cls) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            return ExcelImport.insertIntoEntity1(startRow, property, workbook, cls);
        }
    }
}
