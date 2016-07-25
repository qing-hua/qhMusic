package com.example.qinghua_music;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import database.MyOpenHelper;
import modul.Mp3Info;
import playerservice.MusicOperator;
import playerservice.MyPllayerService;
import user_defined.BaseActivity;
import user_defined.Constant;
import user_defined.GlobalFlag;
import user_defined.PlayerLayoutView;
import utils.ActivityCollecter;
import utils.Downloader;
import utils.GetMusicList;
import utils.MyContentHandler;
import utils.Myapplication;

public class MainActivity extends BaseActivity {
	PlayerLayoutView mp3Player_view ;
	List<Mp3Info> mp3Infos ;
	DrawerLayout drawer_layout ;
	MusicOperator mBinder ;
	SeekBar seekBar ;
	Button music_start ;
	Button local_music ;
	Button onLine_music ;
	Button downloadManage ;
	Button setting ;
	Intent bindingIntent ;
	private int mbackKeyPressedTimes = 0 ;
	FragmentManager fragmentManager = null ;
	
	MyOpenHelper mHelper = null ;
	SQLiteDatabase db = null ;
	
	Downloader downloader ;
	static List<Mp3Info> onlineMp3infos = new ArrayList<Mp3Info>();
	static List<Mp3Info> downloadingMp3infos = new ArrayList<Mp3Info>();
	
	ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mBinder = (MusicOperator)service ;
		//	mBinder.doLoad(seekBar, music_start);
			mBinder.doLoad(GlobalFlag.seekBar, GlobalFlag.music_start);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainactivity_layout);
		
		mHelper = new MyOpenHelper(this, "musiclist.db", null, 1);
		db = mHelper.getWritableDatabase();
		initplayingModel();
		initOnlineMusicList();
		//GlobalFlag.position4histroyMusic作为mp3info 在historymusic表格中的唯一标识 通过它来准确的进行数据的删除、查找 
		initPosition4historymusic();
		
		seekBar = (SeekBar)findViewById(R.id.playing_progress);
		music_start = (Button)findViewById(R.id.music_start);
		GlobalFlag.seekBar = seekBar ;
		GlobalFlag.music_start = music_start ;
		drawer_layout = (DrawerLayout)findViewById(R.id.drawer_layout);
		local_music = (Button)findViewById(R.id.local_music);
		onLine_music = (Button)findViewById(R.id.online_music);
		setting = (Button)findViewById(R.id.seting);
		
		downloadManage = (Button)findViewById(R.id.download_manage);
		
		bindingIntent = new Intent(MainActivity.this, MyPllayerService.class);
		bindService(bindingIntent, serviceConnection, BIND_AUTO_CREATE);
		
	//	mp3Player_view = (LinearLayout)findViewById(R.id.mp3Player_view);
		mp3Player_view = (PlayerLayoutView)findViewById(R.id.mp3Player_view);
	
		mp3Infos = new GetMusicList().getExtraStorageMusicList() ;	
		fragmentManager = getFragmentManager();
		defaultFragment();
		
		local_music.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				LocalFragment localFragment = new LocalFragment();
				FragmentTransaction toAll = fragmentManager.beginTransaction();
				toAll.replace(R.id.container_inMainactivity, localFragment);
				toAll.addToBackStack(null);
				toAll.commit();
			}
		});
		
		onLine_music.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Online_frag_content online_frag_content = new Online_frag_content(onlineMp3infos ,db);
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.replace(R.id.container_inMainactivity, online_frag_content);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});
		
		downloadManage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DownloadFragment downloadFragment = new DownloadFragment(downloadingMp3infos);
				FragmentTransaction transaction = fragmentManager.beginTransaction();
				transaction.replace(R.id.container_inMainactivity, downloadFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		});

		setting.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawer_layout.openDrawer(Gravity.RIGHT);
			}
		});
	}
	
	//取出上次关闭程序时的播放模式
	public void initplayingModel(){
		Cursor cursor = db.query("stateOfPlayingType", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			GlobalFlag.TYPE_PLAYING = cursor.getInt(cursor.getColumnIndex("typeOfPlaying"));
		}
	}
	
	public void initPosition4historymusic(){
		//初始化position 避免后面分配的positio重复 
		//之前采用的是让position在数据库表格中自增长，后来发现自增长量不可控 和所需求的逻辑有偏差，实践中发现当表格中有数据删除后 這个自增长量不会智能缩小
		//还是会接着之前的增长，但这个值我们无法控制和利用，
		Cursor positioncursor = db.query("position4historyMusic", null, null, null, null, null,null);
		if(positioncursor != null && positioncursor.getColumnCount() > 0 ){
			if(positioncursor.moveToFirst()){
				GlobalFlag.position4histroyMusic = positioncursor.getInt(positioncursor.getColumnIndex("position"));
			}
		}
		positioncursor.close();
	}
	
	public void savePosition4historyMusic(){
		ContentValues values = new ContentValues() ;
		values.put("position", GlobalFlag.position4histroyMusic);
		db.delete("position4historyMusic", null, null);
		db.insert("position4historyMusic", null, values);
	}
	
	public void defaultFragment(){
		LocalFragment localFragment = new LocalFragment();
		FragmentTransaction toAll = fragmentManager.beginTransaction();
		toAll.replace(R.id.container_inMainactivity, localFragment);
		toAll.addToBackStack(null);
		toAll.commit();
	}
	
	public void initOnlineMusicList(){
		downloader = new Downloader();
		final Handler handler = new Handler();
		final Runnable r = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this, "在线服务器异常",Toast.LENGTH_SHORT ).show();
			}
		};
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				String xmlFile = downloader.downloadXML(Constant.MUSTICLIST_URL);
				if(xmlFile != null){
					parseXML(xmlFile);
				}else{
					handler.post(r);
				}
			}
		}.start();
	}
	
	public void parseXML(String xmlFile){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			XMLReader reader = factory.newSAXParser().getXMLReader() ;
			MyContentHandler myContentHandler = new MyContentHandler(onlineMp3infos);
			reader.setContentHandler(myContentHandler);
			reader.parse(new InputSource(new StringReader(xmlFile)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		bindService(bindingIntent, serviceConnection, BIND_AUTO_CREATE);
		super.onResume();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		unbindService(serviceConnection);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//这里暂停下是为了从任何界面退出程序时 mp3先关闭
		mBinder.doPause();
		mBinder.doSaveLatalyMusic();
		mp3Player_view.doUnbinding4palyerview();
		savePosition4historyMusic();
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
	//	super.onBackPressed();
		if(mbackKeyPressedTimes == 0){
			mbackKeyPressedTimes = 1;
			Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			new Thread(){
				@Override
				public void run() {
					// TODO Auto-generated method stub
					super.run();
					try {
						sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally{
						mbackKeyPressedTimes = 0 ;
					}
				}
			}.start();
		}else{
			mBinder.doPause();
			ActivityCollecter.finishALL();
		 }
	}
	
	public void doPlay4subfrag(Mp3Info mp3Info){
		mBinder.doInitMp3info(mp3Info);
		mBinder.doSetDefault();
		mBinder.doStart(GlobalFlag.seekBar, GlobalFlag.music_start, mp3Info.getPath());
	}
	
	public void doAddData2DownloadMp3Infos(Mp3Info mp3Info){
		downloadingMp3infos.add(mp3Info);
	}
	public void doRemoveData2DownloadMp3Infos(Mp3Info mp3Info){
		downloadingMp3infos.remove(mp3Info);
	}
}
