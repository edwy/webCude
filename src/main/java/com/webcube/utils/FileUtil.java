package com.webcube.utils;

import java.io.*;

/**
 * Created by
 *
 * @author Edwin Yang on
 *         2017/10/25 0025.
 */
public class FileUtil {

    private static String matches = "[A-Za-z]:\\\\[^:?\"><*]*";

    /**
     * 创建文件目录
     */
    public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建文件
     */
    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return false;
        }
        if (filePath.endsWith(File.separator)) {
            return false;
        }
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        try {
            if (file.createNewFile()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     *文件内容写入
     */
    public static boolean writeFile(String filePath, String contents){

        boolean result = true;
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(filePath,true));
            out.write(contents+"\r\n");
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                result = false;
                e.printStackTrace();
            }
        }
        return result;
    }


}
