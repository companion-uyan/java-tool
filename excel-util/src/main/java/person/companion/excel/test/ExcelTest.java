package person.companion.excel.test;

import cn.hutool.core.io.resource.ClassPathResource;
import person.companion.excel.function.Handle;
import person.companion.excel.function.Valid;
import person.companion.excel.model.ExcelImportR;
import person.companion.excel.model.ImportParam;
import person.companion.excel.util.ExcelUtil;
import org.junit.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title:
 * Author: companion
 * Written by: 2022/8/3 15:03
 */
public class ExcelTest {
    final InputStream inputStream = new ClassPathResource("user-info.xlsx").getStream();

    /**
     * 简单导入
     */
    @Test
    public void testImport() throws Exception {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(0, "name");
        map.put(1, "sex");
        map.put(2, "age");
        map.put(3, "phone");
        List<UserInfo> excelR = ExcelUtil.parseExcel(1, map, inputStream, UserInfo.class);
        System.out.println(excelR);
    }

    /**
     * 有数据处理的导入
     */
    @Test
    public void testHandle() throws Exception {
        Map<Integer, ImportParam<UserHandleInfo>> params = new HashMap<>();
        Handle<UserHandleInfo> nameHandle = (userHandleInfo, cellValue) -> userHandleInfo.setName(cellValue + "-handle");

        Handle<UserHandleInfo> sexHandle = (userHandleInfo, cellValue) -> userHandleInfo.setSex("男".equals(cellValue));

        Valid<UserHandleInfo> nameValid = (userHandleInfo, cellValue, msg) -> {
            if ("刘镜渊".equals(cellValue)) {
                msg.append("刘镜渊-姓名验证测试；");
                return false;
            }

            return true;
        };

        Valid<UserHandleInfo> sexValid = (userHandleInfo, cellValue, msg) -> {
            if (!"男".equals(cellValue) && !"女".equals(cellValue)) {
                msg.append("性别只能是男、女；");
                return false;
            }

            return true;
        };

        params.put(0, new ImportParam<>("name", nameHandle, nameValid));
        params.put(1, new ImportParam<>("sex", sexHandle, sexValid));
        params.put(2, new ImportParam<>("age", null, null));
        params.put(3, new ImportParam<>("phone", null, null));

        ExcelImportR<UserHandleInfo> list = ExcelUtil.parseExcel1(1, params, inputStream, UserHandleInfo.class);
        System.out.println(list);
    }
}
