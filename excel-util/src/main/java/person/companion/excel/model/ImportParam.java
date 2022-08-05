package person.companion.excel.model;

import person.companion.excel.function.Handle;
import person.companion.excel.function.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Title: 数据导入参数
 * Author: companion
 * Written by: 2022/8/3 15:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportParam<T> {
    // 属性名
    private String property;

    // 数据处理方法
    private Handle<T> handle;

    // 数据验证
    private Valid<T> valid;
}
