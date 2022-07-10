package com.pt.tool;
import com.pt.pojo.*;
import java.net.*;
import java.io.IOException;
import java.io.*;
import com.alibaba.fastjson.*;
import java.util.*;

public class BiliParser {
	private static final String DOWNLOAD_API = "https://api.bilibili.com/x/player/playurl";


	private static final String PARSE_API = "https://api.bilibili.com/x/web-interface/view";


	public VideoView inspect(String bv) {
		VideoView videoView = new VideoView();
		try {
			URL url = new URL(PARSE_API+"?bvid="+bv);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();		
			conn.setRequestMethod("GET");
			conn.setReadTimeout(2000);
			InputStream stream  = conn.getInputStream();              
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder sb = new StringBuilder();
			String getReader = null;
			while ((getReader=reader.readLine())!=null){
				sb.append(getReader);
			}            	
			Result<VideoView> result = JSON.parseObject(sb.toString(), new TypeReference<Result<VideoView>>(){});
			if(result.getCode()!=0)
				return null;
			videoView = result.getData();
			return videoView;
		}
		catch(IOException e){
			return null;	
		}
	}
	
    /*
    * 整体解析
    */
	public VideoPlayerUrl parse(VideoView videoView) {
		VideoPlayerUrl videoPlayerUrl = new VideoPlayerUrl();	
		try {		
			URL url = new URL(DOWNLOAD_API+"?bvid="+videoView.getBvid()+"&cid="+videoView.getCid()+"&qn=80&fnever=0&fourk=1"/*+"&_="+String.valueOf(new Date().getTime())*/);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();		
			conn.setRequestMethod("GET");
			conn.setReadTimeout(2000);
			conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
			//conn.addRequestProperty("accept-encoding","gzip, deflate, br");
			conn.addRequestProperty("accept-language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
			
			conn.addRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
			
			InputStream stream  = conn.getInputStream();              
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder sb = new StringBuilder();
			String getReader = null;
			while ((getReader=reader.readLine())!=null){
				sb.append(getReader);
			}          	
			Result<String> result = (Result<String>)JSON.parseObject(sb.toString(), new TypeReference<Result<String>>(){});
			
			if(result.getCode()!=0)
				return null;
				
			JSONObject jsonObject = JSONObject.parseObject(result.getData());
			videoPlayerUrl.setUrl(jsonObject.getJSONArray("durl").getJSONObject(0).getString("url"));//(jsonObject.getString("durl"));
				
			return videoPlayerUrl;
		}
		catch(IOException e){
			return null;	
		}
	}
    
    public List<VideoPlayerUrl> parsePart(VideoView videoView, Boolean soundOnly) {
        VideoPlayerUrl videoPlayerUrl = new VideoPlayerUrl();   
        try {       
            URL url = new URL(DOWNLOAD_API+"?bvid="+videoView.getBvid()+"&cid="+videoView.getCid()+"&qn=80&fnever=0&fourk=1&fnval=16"/*+"&_="+String.valueOf(new Date().getTime())*/);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();      
            conn.setRequestMethod("GET");
            conn.setReadTimeout(2000);
            conn.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            //conn.addRequestProperty("accept-encoding","gzip, deflate, br");
            conn.addRequestProperty("accept-language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");

            conn.addRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

            InputStream stream  = conn.getInputStream();              
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String getReader = null;
            while ((getReader=reader.readLine())!=null){
                sb.append(getReader);
            }           
            Result<String> result = (Result<String>)JSON.parseObject(sb.toString(), new TypeReference<Result<String>>(){});

            if(result.getCode()!=0)
                return null;

            JSONObject jsonObject = JSONObject.parseObject(result.getData());
            JSONArray audioArray = jsonObject.getJSONObject("dash").getJSONArray("audio");            
            List<VideoPlayerUrl> videoPlayerUrlList = new ArrayList<>();
            for (Object object : audioArray) { 
                videoPlayerUrlList.add(new VideoPlayerUrl(((JSONObject)object).getString("baseUrl")));
            }
            return videoPlayerUrlList;
        }
        catch(IOException e){
            return null;    
        }
	}
}
