package com.webcube.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by
 * @author Edwin Yang on
 *         2017/10/25 0025.
 */
public class FileUtil {
    public static String filePath = "D:\\eBook\\";

    public void method1() {
        FileWriter fileWriter = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File file = new File(filePath);
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("追加内容");
        printWriter.flush();
        try {
            fileWriter.flush();
            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
