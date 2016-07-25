package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.util.Log;
import modul.LrcContentInfo;

public class LrcOperator {
	
	List<LrcContentInfo> lrcContentInfos ;
	LrcContentInfo lrcContentInfo ;
	public LrcOperator() {
		lrcContentInfos = new ArrayList<LrcContentInfo>();
		lrcContentInfo = new LrcContentInfo();
	}
	
	public int getLrcTime(String time){
		int lrcTime = 0 ;
		String mTime = time ;
		mTime = mTime.replace(":", ".");
		mTime = mTime.replace(".", "@");
		String[] timedata = mTime.split("@");
		lrcTime = (Integer.parseInt( timedata[0]) * 60 + Integer.parseInt(timedata[1])) * 1000 +  Integer.parseInt(timedata[2]) * 10 ; 
		return lrcTime ;
	}

	public String readLrc(String path){
		StringBuilder stringBuilder = new StringBuilder();
		String lrcpath = path.replace(".mp3", ".lrc");
		File lrcFile = new File(lrcpath);
		FileInputStream in = null ;
		if(lrcFile.exists()){
			try {
				in = new FileInputStream(lrcFile);
				BufferedReader reader = new BufferedReader(new InputStreamReader(in ,"GBK"));
				String s = "" ;
				while((s = reader.readLine()) != null){
					s = s.replace("[", "");
					s = s.replace("]", "@");
					String[] data = s.split("@");
					if(data.length > 1){
						lrcContentInfo.setLrcStr(data[1]);
						lrcContentInfo.setLrcTime(getLrcTime(data[0]));
						lrcContentInfos.add(lrcContentInfo);
					}
					lrcContentInfo = new LrcContentInfo();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stringBuilder.append("没有歌词文件，赶紧去下载！...");  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				stringBuilder.append("没有读取到歌词哦！");  
			} finally{
				try {
					if(in != null){
						in.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else {
			String err = "歌词文件不存在" ;
			return err ;
		}
		
		return stringBuilder.toString();
	}

	public List<LrcContentInfo> getLrcContentInfos(){
		return lrcContentInfos ;
	}
}
