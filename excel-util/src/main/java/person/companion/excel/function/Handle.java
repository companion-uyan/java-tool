package person.companion.excel.function;

/**
 * Title: 数据处理
 * Author: companion
 * Written by: 2022/8/3 15:41
 */
@FunctionalInterface
public interface Handle<T> {
    void handle(T t, String cellValue);
}
