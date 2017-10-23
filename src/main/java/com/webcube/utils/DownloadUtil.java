package com.webcube.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 本例使用的是基于JDK HttpURLConnection的同步下载，即按顺序下载
 * 如果同时下载多个任务，可以使用多线程
 * @author aK1ng
 *
 */
public class DownloadUtil {

    public void setUrl(String url){

    }
    public static void main(String[] args) {
        // 下载列表
        Vector<String> downloadList = new Vector<String>();
        // 添加下载地址
        for(int i = 0;i<269;i++){
            downloadList.add("http://www.bookbao.cc/book.php?txt=/TXT/%D0%DE%D5%E6%B9%E9%C0%B4.txt&yeshu="+i);
        }
        download(downloadList);
    }

    /**
     * 下载
     */
    static void download(Vector<String> downloadList){
        // 线程池
        ExecutorService pool = null;
        HttpURLConnection connection = null;
        //循环下载
        try {
            for (int i = 0; i < downloadList.size(); i++) {
                pool = Executors.newCachedThreadPool();
                final String url = downloadList.get(i);
                String filename = "111";//getFilename(downloadList.get(i));
                System.out.println("正在下载第" + (i+1) + "个文件，地址：" + url);
                Future<HttpURLConnection> future = pool.submit(new Callable<HttpURLConnection>(){
                    @Override
                    public HttpURLConnection call() throws Exception {
                        HttpURLConnection connection = null;
                        connection = (HttpURLConnection) new URL(url).openConnection();
                        connection.setConnectTimeout(10000);//连接超时时间
                        connection.setReadTimeout(10000);// 读取超时时间
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setRequestMethod("GET");
                        //断点续连,每次要算出range的范围,请参考Http 1.1协议
                        //connection.setRequestProperty("Range", "bytes=0");
                        connection.connect();
                        return connection;
                    }
                });
                connection = future.get();
                System.out.println("下载完成.响应码:"+ connection.getResponseCode());
                // 写入文件
                writeFile(new BufferedInputStream(connection.getInputStream()), URLDecoder.decode(filename,"UTF-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
            if (null != pool) {
                pool.shutdown();
            }
        }
    }

    /**
     * 通过截取URL地址获得文件名
     * 注意：还有一种下载地址是没有文件后缀的，这个需要通过响应头中的
     * Content-Disposition字段 获得filename，一般格式为："attachment; filename=\xxx.exe\"
     * @param url
     * @return
     */
    static String getFilename(String url){
        return ("".equals(url) || null == url) ? "" : url.substring(url.lastIndexOf("/") + 1,url.length());
    }

    /**
     * 写入文件
     */
    static void writeFile(BufferedInputStream bufferedInputStream,String filename){
        //创建本地文件
        File destfileFile = new File("D:\\temp\\download\\"+ filename);
        if (destfileFile.exists()) {
            destfileFile.delete();
        }
        if (!destfileFile.getParentFile().exists()) {
            destfileFile.getParentFile().mkdir();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(destfileFile);
            byte[] b = new byte[1024];
            int len = 0;
            // 写入文件
            System.out.println("开始写入本地文件.");
            while ((len = bufferedInputStream.read(b, 0, b.length)) != -1) {
                System.out.println("正在写入字节：" + len);
                fileOutputStream.write(b, 0, len);
            }
            System.out.println("写入本地文件完成.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                if (null != bufferedInputStream) {
                    bufferedInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}