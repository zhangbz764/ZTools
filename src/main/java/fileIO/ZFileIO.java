package fileIO;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * file input & output
 *
 * @author zhangbz ZHANG Baizhou
 * @project shopping_mall
 * @date 2021/8/4
 * @time 16:10
 */
public class ZFileIO {

    /**
     * write String to a local file
     *
     * @param filePath path to save a local file
     * @param data     String data
     * @return void
     */
    public static void writeStringToFile(String filePath, String data) {
        try {
            // 创建文件对象
            File file = new File(filePath);

            // 将字符串转换为字节数组，指定UTF-8编码
            byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

            // 写入文件
            Path path = file.toPath();
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("File written successfully.");
        } catch (IOException e) {
            // 处理异常情况
            e.printStackTrace();
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }

    /**
     * read a file and convert to String
     *
     * @param filePath path to a local file
     * @return java.lang.String
     */
    public static String readStringFromFile(String filePath) {
        try {
            // 创建文件对象
            File file = new File(filePath);

            // 检查文件是否存在
            if (!file.exists()) {
                System.err.println("File not found: " + filePath);
                return null;
            }

            // 读取文件内容
            Path path = file.toPath();
            byte[] bytes = Files.readAllBytes(path);

            // 将字节数组转换为字符串，指定UTF-8编码
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            // 处理异常情况
            e.printStackTrace();
            System.err.println("Error reading from the file: " + e.getMessage());
            return null;
        }
    }
}
