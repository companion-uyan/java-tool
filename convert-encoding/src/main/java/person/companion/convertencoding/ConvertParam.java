package person.companion.convertencoding;

import java.io.File;

/**
 * 功能描述: 转换的参数类
 *
 * @author companion
 * Written by : 2021/11/10 19:28
 */
public class ConvertParam {
    // 编码格式
    public static final String[] charset = {"UTF-8", "GBK"};
    // 文件选择器打开的默认打开的目录
    public static String srcPath = System.getProperty("user.dir");
    // 是否转换该文件夹的子目录
    public static boolean isRecursive = true;
    // 输出目录，默认在当前目录创建convert文件夹
    public static String destPath = "D:/convert";
    // 目标编码
    public static String destCharset;
    // 是否覆盖原文件
    public static boolean isOverride;
    // 需要转换的文件或者文件夹
    public static File[] files;
    // 需要转换的文件或者文件夹
    public static File[] selectFiles;
}
