package utils;

import android.content.ContentValues;
import android.database.Cursor;
import modul.Mp3Info;

public class TranslaterOfData {
	
	public ContentValues mp3info2values(Mp3Info mp3Info){
		ContentValues values = new ContentValues();
		values.put("music_id", mp3Info.getId());
		values.put("music_name", mp3Info.getName());
		values.put("music_singer",mp3Info.getSinger());
		values.put("music_path", mp3Info.getPath());
		values.put("music_time", mp3Info.getTime());
		values.put("music_msTime", mp3Info.getMsTime());
		return values;
	}
	
	public ContentValues mp3info2values(Mp3Info mp3Info ,int position){
		ContentValues values = mp3info2values(mp3Info);
		values.put("position", position);
		return values;
	}
	
	public ContentValues mp3info2values4onlineMusic(Mp3Info mp3Info){
		ContentValues values = mp3info2values(mp3Info);
		values.put("music_size",mp3Info.getSize());
		return values;
	}
	
	public Mp3Info musicCursorItem2mp3info(Cursor musicCursorItem){
		Mp3Info mp3Info = new Mp3Info();
		mp3Info.setId(musicCursorItem.getInt(musicCursorItem.getColumnIndex("music_id")));
		mp3Info.setName(musicCursorItem.getString(musicCursorItem.getColumnIndex("music_name")));
		mp3Info.setSinger(musicCursorItem.getString(musicCursorItem.getColumnIndex("music_singer")));
		mp3Info.setPath(musicCursorItem.getString(musicCursorItem.getColumnIndex("music_path")));
		mp3Info.setTime(musicCursorItem.getString(musicCursorItem.getColumnIndex("music_time")));
		mp3Info.setMsTime(musicCursorItem.getInt(musicCursorItem.getColumnIndex("music_msTime")));
		return mp3Info; 
	}
	
	public Mp3Info musicCursorItem2mp3info4historyMusic(Cursor musicCursorItem){
		Mp3Info mp3Info = musicCursorItem2mp3info(musicCursorItem);
		mp3Info.setPosition(musicCursorItem.getInt(musicCursorItem.getColumnIndex("position")));
		return mp3Info;
	}
	
	public Mp3Info musicCursorItem2mp3info4onlineMusic(Cursor musicCursorItem){
		Mp3Info mp3Info = musicCursorItem2mp3info(musicCursorItem);
		mp3Info.setSize(musicCursorItem.getInt(musicCursorItem.getColumnIndex("music_size")));
		return mp3Info;
	}
}
