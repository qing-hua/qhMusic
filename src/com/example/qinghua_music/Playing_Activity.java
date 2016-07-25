package com.example.qinghua_music;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import modul.Mp3Info;
import playerservice.MusicOperator;
import playerservice.MyPllayerService;
import user_defined.BaseActivity;
import user_defined.GlobalFlag;
import user_defined.LrcContentView;
import user_defined.PlayerLayoutView;

public class Playing_Activity extends BaseActivity{
	MusicOperator mBinder;
	SeekBar seekBar = null ;
	Button music_start = null ;
	TextView music_name_textview ;
	PlayerLayoutView playing_activity_playerview ;
	static TextView music_singer_textview ;
	static LrcContentView lrcContentView ;
	static SensorManager sensorManager = null ;
	
	ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBinder = (MusicOperator) service ;
			mBinder.doLoad(seekBar, music_start);
			mBinder.setLrcContentView(music_name_textview, music_singer_textview, lrcContentView);
			mBinder.doInitLrcContent();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playing_layout);
		
		music_name_textview = (TextView)findViewById(R.id.playing_activity_music_name);
		music_singer_textview = (TextView)findViewById(R.id.playing_activity_music_singer);
		lrcContentView = (LrcContentView)findViewById(R.id.lrcContent_textview);
		
		playing_activity_playerview = (PlayerLayoutView)findViewById(R.id.playing_activity_playerview);
		ImageView img = (ImageView)findViewById(R.id.img_singer);
		img.setVisibility(View.GONE);
		seekBar = (SeekBar)findViewById(R.id.playing_progress);
		music_start = (Button)findViewById(R.id.music_start);
		
		Intent intent = new Intent(Playing_Activity.this, MyPllayerService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
	}
	
	SensorEventListener sensorEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			float xValue = Math.abs(event.values[0]);
			float yValue = Math.abs(event.values[1]);
			float zValue = Math.abs(event.values[2]);
			if (xValue > 12 || yValue > 12 || zValue > 12) {
				// 认为用户摇动了手机，触发摇一摇逻辑
				mBinder.doSetDefault();
				mBinder.doNext(seekBar, music_start);
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	//onResume 和 onPause 配合使摇一摇切歌功能只有在当前页面可见时可用
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = new Intent(Playing_Activity.this, MyPllayerService.class);
		bindService(intent, serviceConnection, BIND_AUTO_CREATE);
		//摇一摇切歌
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	//	Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(sensorEventListener, accelerometerSensor,200000);
	//	sensorManager.registerListener(sensorEventListener, magneticSensor,SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unbindService(serviceConnection);
		if(sensorManager != null){
			sensorManager.unregisterListener(sensorEventListener);
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	//	super.onBackPressed();
		Intent intent = new Intent(Playing_Activity.this, MainActivity.class);
		startActivity(intent);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		playing_activity_playerview.doUnbinding4palyerview();
	}

	public TextView getMusic_name_textview() {
		return music_name_textview;
	}

	public TextView getMusic_singer_textview() {
		return music_singer_textview;
	}

	public LrcContentView getLrcContentView() {
		return lrcContentView;
	}

}
