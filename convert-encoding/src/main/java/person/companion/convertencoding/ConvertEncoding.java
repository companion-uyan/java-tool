package person.companion.convertencoding;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

/**
 * 功能描述: 转换文件编码格式
 *
 * @author companion
 * Written by : 2021/11/2 21:28
 */
public class ConvertEncoding {
    public static Frame frame;

    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        frame = new Frame();

        if (ConvertParam.selectFiles == null || ConvertParam.selectFiles.length == 0) {
            ConvertParam.selectFiles = ConvertParam.files;
        }

        for (File file : ConvertParam.selectFiles) {
            isDirectory(file);
        }
    }

    /**
     * 判断是否是文件夹
     *
     * @param file 需要转换的路径或者文件
     */
    private static void isDirectory(File file) {
        // 文件夹
        if (file.isDirectory() && ConvertParam.isRecursive) {
            // 循环遍历目录
            recursiveDirectory(file, file.getParent());
        }

        // 修改编码
        if (file.isFile()) {
            convertFileEncoding(file, file.getParent());
        }
    }

    /**
     * 循环遍历目录
     *
     * @param file     目录
     * @param basePath 基础目录
     */
    private static void recursiveDirectory(File file, String basePath) {
        for (File listFile : Objects.requireNonNull(file.listFiles())) {
            if (listFile.isDirectory()) {
                recursiveDirectory(listFile, basePath);
            }

            if (listFile.isFile()) {
                convertFileEncoding(listFile, basePath);
            }
        }
    }

    /**
     * 文件编码转换
     *
     * @param file     需要转换的文件
     * @param basePath 基础目录
     */
    private static void convertFileEncoding(File file, String basePath) {
        ConvertParam.destPath = ConvertParam.destPath.endsWith("/") ? ConvertParam.destPath : ConvertParam.destPath + "/";
        File destFile = new File(file.getPath().replace(basePath, ConvertParam.destPath));
        // 创建文件夹
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        // 是否覆盖
        if (destFile.exists() && !ConvertParam.isOverride) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFile), ConvertParam.destCharset));
             BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {

            String s;
            while ((s = reader.readLine()) != null) {
                writer.write(s);
                writer.newLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
