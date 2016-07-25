package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.BreakIterator;

import com.example.qinghua_music.MainActivity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import modul.Mp3Info;
import user_defined.GlobalFlag;

public class Downloader {
	
	public static int contentLength = 0 ;
	
	public String  downloadXML(String strUrl){
		InputStream input = getInputStream(strUrl);
		StringBuffer sb = new StringBuffer();
		if(input != null){
			String line = null ;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(input ,"GBK"));
				while((line = reader.readLine()) != null){
					sb.append(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return sb.toString();
		}
		return null ;
	}
	
	public int downloadMp3(final String dirName ,final String fileName ,final String strUrl ,final Mp3Info mp3Info ,final SQLiteDatabase db){
		final Handler handler = new Handler();
		final Runnable r = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(Myapplication.getContext(), "下载完成", Toast.LENGTH_SHORT).show();
				ContentValues values = new TranslaterOfData().mp3info2values(mp3Info);
				db.insert("myMusic", null, values);
			}
		};
		final Runnable rtwo = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(Myapplication.getContext(), "网络资源不存在或已删除", Toast.LENGTH_LONG).show();
				new MainActivity().doRemoveData2DownloadMp3Infos(mp3Info);
			}
		};
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				InputStream input = getInputStream(strUrl);
				if(input != null ){
					File file = new FileOperater().writeFromInput2SDcard(dirName, fileName, input);
					if(file.length() > mp3Info.getSize() || file.length() == mp3Info.getSize()){
						handler.post(r);
					}
				}else {
					handler.post(rtwo);
				}
			}
		}.start();
		return GlobalFlag.download_success ;
	}
	
	public void downloadLrc(final Mp3Info mp3Info ,String path){
		final String lrcPath = path.replace(".mp3", ".lrc");
		new Thread(){
			public void run() {
				File f = new File(Environment.getExternalStorageDirectory() + "qhMusic/" + mp3Info.getName().replace(".mp3", ".lrc")) ;
				if(f.exists()){
					f.delete();
				}	
				InputStream input = getInputStream(lrcPath);
				if(input != null){	
					File file = new FileOperater().writeFromInput2SDcard("qhMusic/", mp3Info.getName().replace(".mp3", ".lrc"), input);
					if(file == null || file.length() == 0){
						file.delete();
					}
				}
			}
		}.start();
	} 
	
	public InputStream getInputStream(String strUrl){
		InputStream input = null ;
		HttpURLConnection conn = null ;
		try {
			URL url = new URL(strUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
	//		conn.setRequestProperty("Accept-Charset", "UTF-8");
	//		conn.setRequestProperty("contentType", "UTF-8");
			if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
				input = conn.getInputStream();
				contentLength = conn.getContentLength();
			} else if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND){
				return null ;
			}
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return input ;
	}
}
