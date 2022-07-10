package com.pt.tool;
import com.pt.pojo.*;
import java.net.*;
import java.io.*;
import android.os.storage.*;
import android.os.*;
import android.provider.MediaStore;
import android.widget.*;
import com.pt.bilibilidownloader.*;
import android.content.*;
import android.net.*;

import androidx.core.content.ContextCompat;

import java.util.*;

public class BiliDownloader {
	public static String STORAGE_PATH =
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
			+ "/BiliBili/";

	//MediaStore.Downloads

    public BiliDownloader () {
        initialize();
    }
    
    private void initialize () {
        File baseFile = new File(STORAGE_PATH);
        if(!baseFile.exists())
            baseFile.mkdir();
    }
    
    public String download(List<Video> videoList) {
        for(int i = 0; i < videoList.size(); i++) {
            download(videoList.get(i), "mp3", String.valueOf(i));
        }
        return "下载成功，一共下载了品质不同的"+videoList.size()+"个音频";
    }	
    
    public String download(Video video) {
        return download(video, "flv", "");
    }
    
	private String download(Video video, String typeName, String tailName) {		
        HttpURLConnection conn = null;
		BufferedInputStream  bis =null;
		BufferedOutputStream bos=null;
		try {
			URL url = new URL(video.getVideoPlayerUrl().getUrl());
			conn = (HttpURLConnection) url.openConnection();	
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);	
			conn.setReadTimeout(60000);	
			conn.setDoInput(true);
			conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
			conn.addRequestProperty("accept-encoding","gzip, deflate, br");
			conn.addRequestProperty("accept-language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
			conn.addRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
			conn.addRequestProperty("referer","https://www.bilibili.com/video/"+video.getVideoView().getBvid());
			conn.addRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			int contentLength = conn.getContentLength();
			System.out.println("文件的大小是:"+contentLength);
			if (contentLength>32) {		
				InputStream is= conn.getInputStream();
				bis = new BufferedInputStream(is);
                String fileName = KeywordModifier.checkAndModify(video.getVideoView().getTitle()+tailName);
				File typeFile = new File(STORAGE_PATH + typeName.toUpperCase());
                if(!typeFile.exists())
                    typeFile.mkdir();                
                File file = new File(STORAGE_PATH + typeName.toUpperCase() + "/" + fileName +"."+typeName);//新建文件
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
