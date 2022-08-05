package person.companion.convertencoding;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * 功能描述: 创建一个Frame来选择需要转换编码的文件与编码格式
 *
 * @author companion
 * Written by : 2021/11/9 20:01
 */
public class Frame extends Application {
    public Frame() {
        // 窗口
        JDialog dialog = new JDialog();
        // 窗口大小
        dialog.setSize(540, 270);
        // 关闭才执行主程序
        dialog.setModal(true);
        // 关闭窗口时关闭程序
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // 创建container
        createContainer(dialog);
        // 居中，注意代码位置
        dialog.setLocationRelativeTo(null);
        // 可见，注意代码位置
        dialog.setVisible(true);
    }

    // public static void main(String[] args) {
    //     new Frame();
    // }

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    /**
     * 创建container
     *
     * @param dialog 窗口
     */
    private void createContainer(JDialog dialog) {
        // 创建文件选择器
        JFileChooser chooser = createFileChooser();
        Container container = new Container();
        // 创建源文件行
        createSourceFileSelectRow(chooser, container);
        // 创建输出文件行
        createDestFileSelectRow(chooser, container);
        // 创建编码选择行
        createCharsetRow(container);
        // 创建开始转换按钮
        createStartButton(dialog, container);

        dialog.add(container);
    }

    /**
     * 创建是否覆盖源文件选框
     *
     * @param container 容器
     */
    private void createOverrideCheckBox(Container container) {
        Checkbox checkbox = new Checkbox("override");
        checkbox.setBounds(20, 120, 150, 20);
        checkbox.addItemListener(item -> ConvertParam.isOverride = item.getStateChange() == ItemEvent.SELECTED);

        container.add(checkbox);
    }

    /**
     * 创建输出文件行
     *
     * @param chooser   文件选择器
     * @param container 容器
     */
    private void createDestFileSelectRow(JFileChooser chooser, Container container) {
        JLabel label = new JLabel("请选择输出目录：");
        label.setBounds(20, 70, 130, 20);
        container.add(label);

        // 文本
        JTextArea area = new JTextArea();
        container.add(area);
        area.setText(ConvertParam.destPath);
        area.setBounds(130, 70, 300, 20);
        area.addPropertyChangeListener(evt -> ConvertParam.destPath = area.getText());

        // 按钮
        JButton button = new JButton("···");
        container.add(button);
        button.setBounds(440, 70, 50, 20);
        button.addActionListener(e -> {
            // 只能选择文件夹
            chooser.setMultiSelectionEnabled(false);
            chooser.setCurrentDirectory(new File(area.getText()));
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            //打开文件浏览器，点击取消则返回1
            int status = chooser.showOpenDialog(null);
            if (JFileChooser.APPROVE_OPTION == status) {
                ConvertParam.destPath = chooser.getSelectedFile().getAbsolutePath();
                area.setText(ConvertParam.destPath);
            }
        });
    }

    /**
     * 创建文件选择器
     *
     * @return 文件选择器
     */
    private JFileChooser createFileChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setDialogTitle("请选择文件：");
        chooser.setMultiSelectionEnabled(true);
        chooser.setApproveButtonText("确定");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        return chooser;
    }

    /**
     * 创建源文件选择行
     *
     * @param chooser   文件选择器
     * @param container 容器
     */
    private void createSourceFileSelectRow(JFileChooser chooser, Container container) {
        JLabel label = new JLabel("请选择文件：");
        label.setBounds(20, 20, 100, 20);
        container.add(label);

        // 文本
        JTextArea area = new JTextArea();
        container.add(area);
        area.setText(ConvertParam.srcPath);
        area.setBounds(130, 20, 300, 20);
        area.addPropertyChangeListener(evt -> {
            ConvertParam.srcPath = area.getText();
            ConvertParam.files = new File[]{new File(area.getText())};
        });

        // 按钮
        JButton button = new JButton("···");
        container.add(button);
        button.setBounds(440, 20, 50, 20);
        button.addActionListener(e -> {
            // 可以选择文件与文件夹
            chooser.setMultiSelectionEnabled(true);
            chooser.setCurrentDirectory(new File(area.getText()));
            chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

            // 打开文件浏览器，点击取消则返回1
            int status = chooser.showOpenDialog(null);
            if (JFileChooser.APPROVE_OPTION == status) {
                ConvertParam.selectFiles = chooser.getSelectedFiles();
                // area.setText(Arrays.toString(ConvertParam.files));
            }
        });
    }

    /**
     * 创建编码选择行
     *
     * @param container 容器
     */
    private void createCharsetRow(Container container) {
        // 创建是否覆盖源文件选框
        createOverrideCheckBox(container);

        // 转换后的文件编码
        JLabel destLabel = new JLabel("转换后编码：");
        destLabel.setBounds(320, 120, 120, 20);
        container.add(destLabel);

        JComboBox<String> destCharsetComboBox = new JComboBox<>(ConvertParam.charset);
        destCharsetComboBox.setBounds(420, 120, 70, 20);
        // 默认第一个编码
        destCharsetComboBox.setSelectedIndex(0);
        ConvertParam.destCharset = ConvertParam.charset[0];
        destCharsetComboBox.addActionListener(e -> {
            if (destCharsetComboBox.getSelectedIndex() >= 0) {
                ConvertParam.destCharset = ConvertParam.charset[destCharsetComboBox.getSelectedIndex()];
            }
        });

        container.add(destCharsetComboBox);
    }

    /**
     * 创建开始转换按钮
     *
     * @param container 窗体
     */
    private void createStartButton(JDialog dialog, Container container) {
        JButton button = new JButton("开始转换");
        button.setBounds(250, 170, 100, 25);
        button.addActionListener(e -> {
            // 关闭窗口
            dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
        });

        container.add(button);
    }
}
