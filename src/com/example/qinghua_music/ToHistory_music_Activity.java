package com.example.qinghua_music;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import database.MyOpenHelper;
import modul.Mp3Info;
import playerservice.MusicOperator;
import playerservice.MyPllayerService;
import user_defined.BaseActivity;
import user_defined.GlobalFlag;
import user_defined.Mp3itemAdapter;
import utils.TranslaterOfData;

public class ToHistory_music_Activity extends BaseActivity{
	
	ListView history_music_listview ;
	MusicOperator mBinder = null ;
	List<Mp3Info> historyMp3infos = new ArrayList<Mp3Info>();
	Mp3itemAdapter mp3itemAdapter = null ;
	int selectPostion = 0 ;
	
	MyOpenHelper mHelper = null;
	SQLiteDatabase db = null ;

	ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBinder = (MusicOperator)service ;
		}
	};
 	 
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.history_music_activity_layout);
		Intent intent = new Intent(ToHistory_music_Activity.this, MyPllayerService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		
		mHelper = new MyOpenHelper(ToHistory_music_Activity.this, "musiclist.db", null, 1);
		db = mHelper.getWritableDatabase();
//		getMusicFromHistoryMusicList();
		
		history_music_listview = (ListView)findViewById(R.id.history_music_listview);
		mp3itemAdapter = new Mp3itemAdapter(ToHistory_music_Activity.this, R.layout.mp3item_layout,historyMp3infos);
		history_music_listview.setAdapter(mp3itemAdapter);
		history_music_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Mp3Info mp3Info = historyMp3infos.get(position);
				mBinder.doInitMp3info(mp3Info);
				mBinder.doSetDefault();
				mBinder.doStart(GlobalFlag.seekBar, GlobalFlag.music_start, mp3Info.getPath());
			}
		});
		history_music_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				showDialog4longClick(position);
				return true;
			}
		});
	}
	public void getMusicFromHistoryMusicList(){
		historyMp3infos.removeAll(historyMp3infos);
		Cursor historyMusicCursor = db.query("historyMusic", null, null, null, null, null, null);
		Mp3Info mp3Info = null ;
		if(historyMusicCursor != null && historyMusicCursor.getCount() > 0){
			if(historyMusicCursor.moveToFirst()){
				do{
					mp3Info = new TranslaterOfData().musicCursorItem2mp3info4historyMusic(historyMusicCursor);
					historyMp3infos.add(mp3Info);
				}while(historyMusicCursor.moveToNext());
			}
		}
		historyMusicCursor.close();
	}
	
	public void showDialog4longClick(final int position){
		final Mp3Info mp3Info = historyMp3infos.get(position);
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage("从列表中删除歌曲");
//		builder.setCancelable(true);
		builder.setNegativeButton("NO", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		builder.setPositiveButton("OK", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				db.delete("historyMusic", "position = ?", new String [] {mp3Info.getPosition() + ""});
				historyMp3infos.remove(position);
				mp3itemAdapter.notifyDataSetChanged();
				System.out.println(db.delete("historyMusic", "position = ?", new String [] {position + ""}));
				
			}
		});
		builder.create().show();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		selectPostion = history_music_listview.getSelectedItemPosition();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getMusicFromHistoryMusicList();
		mp3itemAdapter.notifyDataSetChanged();
		history_music_listview.setSelection(selectPostion);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(serviceConnection);
		super.onDestroy();
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//这里是为了点击back键不会销毁掉这个activity 配合 onPause 和 onResume来实现将listview定位在上次浏览的位置
		Intent intent = new Intent(ToHistory_music_Activity.this, MainActivity.class);
		startActivity(intent);
	}
}
