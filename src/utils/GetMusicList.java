package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import modul.Mp3Info;

public class GetMusicList {

	List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>() ;
	Context context = Myapplication.getContext();
	
	public List<Mp3Info> getExtraStorageMusicList(){
		
		ContentResolver musicResolver = context.getContentResolver();
		Cursor musicCursor = musicResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
				null, null ,null);
		int id = 0 ;
		try {
			if(musicCursor != null && musicCursor.getCount() > 0){
				for(musicCursor.moveToFirst() ; !musicCursor.isAfterLast() ; musicCursor.moveToNext()){
					Mp3Info mp3Info = new Mp3Info() ;
					
				//	mp3Info.setId(musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)));
					mp3Info.setId(id);
					mp3Info.setName(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
					mp3Info.setPath(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
					mp3Info.setSinger(musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
					
					int time = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION)) ;
					int second = time % 60000 /1000 ;
					int minute = time / 60000 ;
					String musicTime = ":" ;
					if(minute == 0){
						musicTime = "00" + musicTime ;
					}
					else if(0 < minute && minute < 10){
						musicTime = "0" + minute + musicTime ;
					}
					else {
						musicTime = minute + musicTime ;
					}
					if(second < 10){
						musicTime = musicTime + "0" + second ;
					}
					else {
						musicTime = musicTime + second ;
					}
					mp3Info.setMsTime(time);
					mp3Info.setTime(musicTime);
					File file = new File(mp3Info.getPath());
					if(file.exists()){
						mp3Infos.add(mp3Info);
						id ++ ;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(musicCursor != null){
				musicCursor.close();
			}
		}
		return mp3Infos ;
	}
}
