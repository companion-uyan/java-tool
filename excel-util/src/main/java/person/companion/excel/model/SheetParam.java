package person.companion.excel.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SheetParam {
    // 页面dataGrid查询调用的地址
    private String url;

    // sheet名字
    private String sheetName;

    // 表头参数
    private List<ColumnParam> columns;

    // 需要查询数据的字段
    private List<String> fieldList;

    // 查询参数
    private Map<String, Object> param;
}
