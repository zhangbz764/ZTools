package fileIO;

import java.io.*;

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
        BufferedWriter writer = null;
        File file = new File(filePath);
        // create a new file if doesn't exist
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // write a file
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("file save success");
    }

    /**
     * read a file and convert to String
     *
     * @param filePath path to a local file
     * @return java.lang.String
     */
    public static String readStringFromFile(String filePath) {
        String str = "";
        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            Reader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
            fileReader.close();
            reader.close();
            str = sb.toString();
            return str;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
