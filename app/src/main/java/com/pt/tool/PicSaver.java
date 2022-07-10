package com.pt.tool;
import java.net.*;
import java.io.*;
import android.os.*;

public class PicSaver {
    private static String STORAGE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/BiliBili/";

    public static String save (String name, String url) {
        HttpURLConnection conn = null;
        BufferedInputStream  bis =null;
        BufferedOutputStream bos=null;
        try {
            URL urll = new URL(url);
            conn = (HttpURLConnection) urll.openConnection();    
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);   
            conn.setReadTimeout(60000); 
            conn.setDoInput(true);
            //conn.setDoOutput(true);
            //conn.setDoInput(true);
            conn.addRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            int contentLength = conn.getContentLength();
            System.out.println("文件的大小是:"+contentLength);
            if (contentLength>32) {     
                InputStream is= conn.getInputStream();
                bis = new BufferedInputStream(is);
                String fileName = KeywordModifier.checkAndModify(name);
                File typeFile = new File(STORAGE_PATH + "PIC");
                if(!typeFile.exists())
                    typeFile.mkdir();                
                File file = new File(STORAGE_PATH + "PIC" + "/" + fileName +".jpg");//新建文件
                FileOutputStream fos = new FileOutputStream(file);
                bos= new BufferedOutputStream(fos);
                int b = 0;
                byte[] byArr = new byte[1024];
                while((b= bis.read(byArr))!=-1){
                    bos.write(byArr, 0, b);
                }
                System.out.println("下载的文件的大小是----------------------------------------------:"+contentLength);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "下载失败，"+e.toString();
        }finally{
            try {
                if(bis !=null){
                    bis.close();
                }
                if(bos !=null){
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return String.valueOf("下载成功");
    }
}
