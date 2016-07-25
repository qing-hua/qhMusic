package com.example.qinghua_music;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import modul.Mp3Info;
import playerservice.MusicOperator;
import playerservice.MyPllayerService;
import user_defined.BaseActivity;
import user_defined.GlobalFlag;
import user_defined.Mp3itemAdapter;
import utils.GetMusicList;

public class ToAll_music_Activity extends BaseActivity{
	List<Mp3Info> mp3infos = null;
	ListView all_music_listview ;
	MusicOperator mBinder ;
	Mp3itemAdapter mp3itemAdapter ;
	int selectPostion = 0;
	ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBinder = (MusicOperator)service;
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.all_music_activity_layout);
		Intent intent = new Intent(ToAll_music_Activity.this, MyPllayerService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		all_music_listview = (ListView)findViewById(R.id.all_music_listview);
		mp3infos = new GetMusicList().getExtraStorageMusicList();
		mp3itemAdapter = new Mp3itemAdapter(ToAll_music_Activity.this, R.layout.mp3item_layout, mp3infos);
		all_music_listview.setAdapter(mp3itemAdapter);
		all_music_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Mp3Info mp3Info = mp3infos.get(position);
				Log.d("qhtext", "one postion -->" + position);
				mBinder.doInitMp3info(mp3Info);
				mBinder.doSetDefault();
				mBinder.doStart(GlobalFlag.seekBar, GlobalFlag.music_start, mp3Info.getPath());
			}
		});
		all_music_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Mp3Info mp3Info = mp3infos.get(position);
				showDialog4longClick(mp3Info, position);
				return true;
			}
		});
	}
	
	public void showDialog4longClick(final Mp3Info mp3Info, final int position){
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("提示");
		builder.setMessage("删除歌曲");
		builder.setCancelable(true);
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
				mp3infos.remove(position);
				mp3itemAdapter.notifyDataSetChanged();
				File file = new File(mp3Info.getPath());
				file.delete();
			}
		});
		builder.create().show();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		selectPostion = all_music_listview.getSelectedItemPosition();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		all_music_listview.setSelection(selectPostion);  
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
		Intent intent = new Intent(ToAll_music_Activity.this, MainActivity.class);
		startActivity(intent);
	}
	
	public int getAllMusicMp3infos_length(){
		return mp3infos.size();
	}
}
