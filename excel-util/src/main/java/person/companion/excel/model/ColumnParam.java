package person.companion.excel.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ColumnParam {
    // 标题
    private String title;

    // 宽度
    private Integer width;

    // 单元格合并开始行
    private Short firstRow;

    // 单元格合并结束行
    private Short lastRow;

    // 单元格合并开始列
    private Short firstCol;

    // 单元格合并结束列
    private Short lastCol;
}
