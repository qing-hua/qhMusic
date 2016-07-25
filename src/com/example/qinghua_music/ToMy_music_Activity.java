package com.example.qinghua_music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
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

public class ToMy_music_Activity extends BaseActivity{
	ListView myMusic_listview ;
	Mp3itemAdapter adapter ;
	List<Mp3Info> myMusicMp3infos = new ArrayList<Mp3Info>();
	MyOpenHelper mHelper;
	SQLiteDatabase db ;
	int selectPostion = 0 ;
	MusicOperator mBinder ;
	ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBinder = (MusicOperator) service ;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_music_activity_layout);
		
		Intent intent = new Intent(ToMy_music_Activity.this, MyPllayerService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		
		myMusic_listview = (ListView)findViewById(R.id.myMusic_listview);
		mHelper = new MyOpenHelper(ToMy_music_Activity.this, "musiclist.db", null, 1);
		db = mHelper.getWritableDatabase();
		getMyMp3infos();
		adapter = new Mp3itemAdapter(ToMy_music_Activity.this, R.layout.mp3item_layout, myMusicMp3infos);
		myMusic_listview.setAdapter(adapter);
		myMusic_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Mp3Info mp3Info = myMusicMp3infos.get(position);
				mBinder.doInitMp3info(mp3Info);
				mBinder.doSetDefault();
				mBinder.doStart(GlobalFlag.seekBar, GlobalFlag.music_start,mp3Info.getPath());
			}
		});
		myMusic_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Mp3Info mp3Info = myMusicMp3infos.get(position);
				showDialog4longClick(position, mp3Info);
				return true;
			}
		});
		
	}
	
	public void showDialog4longClick(final int position ,final Mp3Info mp3Info){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage("从列表中删除歌曲");
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
				db.delete("myMusic", "music_id = ?", new String [] {mp3Info.getId() + ""});
				myMusicMp3infos.remove(position);
				new File(mp3Info.getPath()).delete();
				adapter.notifyDataSetChanged();
			}
		});
		builder.create().show();
	}
	public void getMyMp3infos(){
		Cursor cursor = db.query("myMusic", null, null, null, null, null, null);
		Mp3Info mp3Info ;
		if(cursor != null && cursor.getCount() > 0){
			if(cursor.moveToFirst()){
				do{
					mp3Info = new TranslaterOfData().musicCursorItem2mp3info(cursor);
					myMusicMp3infos.add(mp3Info);
				}while(cursor.moveToNext());
			}
			cursor.close();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		selectPostion = myMusic_listview.getSelectedItemPosition();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		myMusic_listview.setSelection(selectPostion);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unbindService(serviceConnection);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//这里是为了点击back键不会销毁掉这个activity 配合 onPause 和 onResume来实现将listview定位在上次浏览的位置
		Intent intent = new Intent(ToMy_music_Activity.this, MainActivity.class);
		startActivity(intent);
	}
}
