package person.companion.excel.function;

/**
 * Title: 数据验证
 * Author: companion
 * Written by: 2022/8/3 15:42
 */
@FunctionalInterface
public interface Valid<T> {
    boolean valid(T t, String cellValue, StringBuilder errMsg);
}
