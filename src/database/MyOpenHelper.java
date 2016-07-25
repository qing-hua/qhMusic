package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpenHelper extends SQLiteOpenHelper{
	
	public static final String CREATE_ONLINE_MUSIC_LIST = "create table onlineMusic("
									+ "music_id integer ,"
									+ "music_name text ,"
									+ "music_path text ,"
									+ "music_singer text ,"
									+ "music_time text ,"
									+ "music_size integer,"
									+ "music_msTime integer )" ;
	
	public static final String CREATE_HISTORY_MUSIC_LIST = "create table historyMusic("
									+ "position integer ,"	
									+ "music_id integer ,"
									+ "music_name text ,"
									+ "music_path text ,"
									+ "music_singer text ,"
									+ "music_time text ,"
									+ "music_msTime integer )" ;
	public static final String CREATE_POSITION_CODING4HISTORYMUSIC = "create table position4historyMusic("
									+ "position integer )" ;
	
	public static final String CREATE_LAST_MUSIC = "create table latelyMusic("
									+ "music_id integer ,"
									+ "music_name text ,"
									+ "music_path text ,"
									+ "music_singer text ,"
									+ "music_time text ,"
									+ "music_msTime integer )" ;
	
	public static final String CREATE_MY_MUSIC = "create table myMusic("
									+ "music_id integer ,"
									+ "music_name text ,"
									+ "music_path text ,"
									+ "music_singer text ,"
									+ "music_time text ,"
									+ "music_msTime integer )" ;

	public static final String CREATE_STATE = "create table stateOfPlayingType("
									+ "typeOfPlaying integer )" ;

	public MyOpenHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_ONLINE_MUSIC_LIST);
		db.execSQL(CREATE_HISTORY_MUSIC_LIST);
		db.execSQL(CREATE_POSITION_CODING4HISTORYMUSIC);
		db.execSQL(CREATE_MY_MUSIC);
		db.execSQL(CREATE_LAST_MUSIC);
		db.execSQL(CREATE_STATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
