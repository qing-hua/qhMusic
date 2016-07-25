 package playerservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.qinghua_music.Playing_Activity;
import com.example.qinghua_music.R;

import android.app.Notification.Builder;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import database.MyOpenHelper;
import modul.LrcContentInfo;
import modul.Mp3Info;
import user_defined.GlobalFlag;
import user_defined.LrcContentView;
import utils.GetMusicList;
import utils.LrcOperator;
import utils.Myapplication;
import utils.TranslaterOfData;

public class MyPllayerService extends Service{
	List<Mp3Info> mp3infos = null ;
//	List<Mp3Info> historyMp3infos = new ArrayList<Mp3Info>();
	private Mybinder mbinder = null ;
	MediaPlayer mplayer = null ;
	MyOpenHelper mhelper = null;
	SQLiteDatabase db = null;
	
	//临时记忆住当前mp3info 用于判断mp3info是否有改变
	Mp3Info flagmp3info = null;
	static List<LrcContentInfo> lrcContentInfos = null;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mbinder;
	}
	
	@SuppressLint("NewApi")
	public void setNotification(){
		Builder builder = new Builder(this);
		builder.setContentTitle("ru wu meng xiang");
		builder.setContentText("he bi yuan fang");
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setTicker("successful");
		Notification notification = builder.build();
		notification.bigContentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
		startForeground(1, notification);
	}
	
	class Mybinder extends Binder implements MusicOperator{
		SeekBar seekBar = null ;
		Button music_start = null ;
		Mp3Info mp3Info = null ;
		
		LrcOperator lrcOperator;
		TextView musicNameTextview ;
		TextView musicSingerTextView ;
		LrcContentView mlrcContentView ;
		
		int currentTime = 0;
		int duration = 0 ;
		int lrcIndex = 0;
		
		Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				seekBar.setProgress(mplayer.getCurrentPosition());
				if(GlobalFlag.ISPLAYING){
					music_start.setBackgroundResource(R.drawable.ic_pause);
				}
				else{
					music_start.setBackgroundResource(R.drawable.ic_start);
				}
				handler.postDelayed(runnable, 100);
			}
		};
		//// 空点击让开始按钮复原的具体实现
		Runnable nullOperate = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				GlobalFlag.ISPLAYING = false ;
				music_start.setBackgroundResource(R.drawable.ic_start);
			}
		};
		
		@Override
		public void doStart(SeekBar seekBar , Button music_start ,String path ) {
			// TODO Auto-generated method stub
			if(mplayer == null){
				if(path != null){
					mplayer = new MediaPlayer();
					doInit(seekBar,music_start, path);
				}else{
				// 空点击让开始按钮复原
					this.music_start = music_start ;
					handler.postDelayed(nullOperate, 300);
				}
			}
			else {
				mplayer.start();
			}
		}

		@Override
		public void doPause() {
			// TODO Auto-generated method stub
			if(mplayer != null){
				mplayer.pause();
			}
		}

		@Override
		public void doSetDefault() {
			// TODO Auto-generated method stub
			if(mplayer != null){
				mplayer.stop();
				mplayer.release();
				mplayer = null ;
				GlobalFlag.ISPLAYING = false ;
			}
		}
		
		@Override
		public void doPrev(SeekBar seekBar ,Button music_start) {
			// TODO Auto-generated method stub
			doSetDefault();
			if(this.mp3Info != null){
				int index = 0 ;
				if(GlobalFlag.TYPE_PLAYING == GlobalFlag.ORDER_PLAYING){
					index = this.mp3Info.getId() - 1 ;
					if(index < 0){
						index = index + mp3infos.size();
					}else{
						index = index +  0; 
					}
				}
				else if(GlobalFlag.TYPE_PLAYING == GlobalFlag.LOOPER_PLAYING){
					index = this.mp3Info.getId();
				}
				else if(GlobalFlag.TYPE_PLAYING == GlobalFlag.RANDOM_PLAYING){
					index = (int)(Math.random() * mp3infos.size());
				}
				Mp3Info recordMp3info = mp3infos.get(index);
				doStart(seekBar,music_start, recordMp3info.getPath());
				this.mp3Info = recordMp3info ;
			}else{
				this.music_start = music_start ;
				handler.postDelayed(nullOperate, 300);
			}
		}

		@Override
		public void doNext(SeekBar seekBar ,Button music_start) {
			// TODO Auto-generated method stub
			doSetDefault();
			//播放完后继续播放，初始化 seekBar 和 music_start 
			if(seekBar == null && music_start == null){
				seekBar = this.seekBar ;
				music_start = this.music_start ;
			}
			if(this.mp3Info != null){
				int index = 0 ;
				if(GlobalFlag.TYPE_PLAYING == GlobalFlag.ORDER_PLAYING){
					index = this.mp3Info.getId() + 1 ;
					if(index < mp3infos.size()){
						index = index + 0;
					}else{
						index = index % mp3infos.size() ;
					} 
				}
				else if(GlobalFlag.TYPE_PLAYING == GlobalFlag.LOOPER_PLAYING){
					index = this.mp3Info.getId();
				}
				else if(GlobalFlag.TYPE_PLAYING == GlobalFlag.RANDOM_PLAYING){
						index = (int)(Math.random() * mp3infos.size());
				}
				Mp3Info recordMp3info = mp3infos.get(index) ;
				doStart(seekBar, music_start ,recordMp3info.getPath());
				this.mp3Info = recordMp3info;
			}else{
				this.music_start = music_start ;
				handler.postDelayed(nullOperate, 300);
			}
		}
		
		@Override
		public void doInitMp3info(Mp3Info mp3Info) {
			// TODO Auto-generated method stub
			this.mp3Info = mp3Info ;
		}
		
		@Override
		public void doLoad(SeekBar seekBar , Button music_start) {
			// TODO Auto-generated method stub
			if(!seekBar.equals(null)){
				this.seekBar = seekBar ;
			}
			if(!music_start.equals(null)){
				this.music_start = music_start ;
			}
			if(mplayer != null){
				this.seekBar.setMax(mplayer.getDuration());
		//		this.seekBar.setSecondaryProgress(mplayer.getDuration());
				this.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
						// TODO Auto-generated method stub
						if(fromUser){
							mplayer.seekTo(progress);
						}
					}
				});
				
				handler.post(runnable);
				
			}
		}
		
		@Override
		public void doInit(SeekBar seekBar ,Button music_start ,String path) {
			// TODO Auto-generated method stub

			ContentValues values = new TranslaterOfData().mp3info2values(this.mp3Info , GlobalFlag.position4histroyMusic);
			db.insert("historyMusic", null, values);
			GlobalFlag.position4histroyMusic ++;
			if(mplayer != null){
				try {
					mplayer.setDataSource(path);
					mplayer.setLooping(false);
					mplayer.prepare();
					mplayer.start();
					GlobalFlag.ISPLAYING = true ;
					
		//			setNotification();
					
					doLoad(seekBar, music_start);
					
					mplayer.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							// TODO Auto-generated method stub
							doNext(null, null);
						}
					});
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void doSaveLatalyMusic() {
			// TODO Auto-generated method stub
			if(db != null && this.mp3Info != null){
				ContentValues values = new TranslaterOfData().mp3info2values(this.mp3Info);
				db.delete("latelyMusic", null, null);
				db.insert("latelyMusic", null, values);
			}
		}
		
		public void setMp3Info(Mp3Info mp3Info) {
			this.mp3Info = mp3Info;
		}
		
		public void setLrcContentView(TextView lrcContentview_music_name ,TextView lrcContentview_music_singer ,LrcContentView lrcContentView){
			musicNameTextview = lrcContentview_music_name ;
			musicSingerTextView = lrcContentview_music_singer ;
			mlrcContentView = lrcContentView ;
		}
		
		public void doInitLrcContent(){
			if(mp3Info != null){
				lrcOperator = new LrcOperator();
				lrcOperator.readLrc(mp3Info.getPath());
				lrcContentInfos = lrcOperator.getLrcContentInfos();
				mlrcContentView.setLrcContentInfos(lrcContentInfos);
				flagmp3info = mp3Info ;
				lrcHandler.post(lrcRunnable);
			}
		}
		
		Handler lrcHandler = new Handler();
		Runnable lrcRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				musicNameTextview.setText(mp3Info.getName());
				musicSingerTextView.setText(mp3Info.getSinger());
				if(! flagmp3info.equals(mp3Info)){
					flagmp3info = mp3Info ;
					lrcOperator = new LrcOperator();
					lrcOperator.readLrc(mp3Info.getPath());
					lrcContentInfos = null;
					lrcContentInfos = lrcOperator.getLrcContentInfos();
					mlrcContentView.setLrcContentInfos(lrcContentInfos);
				}
				mlrcContentView.setIndex(getIndex());
				mlrcContentView.invalidate(); 
				lrcHandler.postDelayed(lrcRunnable, 100);
			}
		};
		
		public int getIndex(){
			if(mplayer.isPlaying()) {  
		        currentTime = mplayer.getCurrentPosition();  
		        duration = mplayer.getDuration();  
		    }  
		    if(currentTime < duration) {  
		        for (int i = 0; i < lrcContentInfos.size(); i++) {  
		            if (i < lrcContentInfos.size() - 1) {  
		                if (currentTime < lrcContentInfos.get(i).getLrcTime() && i == 0) {  
		                    lrcIndex = i;  
		                }  
		                if (currentTime > lrcContentInfos.get(i).getLrcTime()  
		                        && currentTime < lrcContentInfos.get(i + 1).getLrcTime()) {  
		                    lrcIndex = i;  
		                }  
		            }  
		            if (i == lrcContentInfos.size() - 1  
		                    && currentTime > lrcContentInfos.get(i).getLrcTime()) {  
		                lrcIndex = i;  
		            }  
		        }  
		    }  
		    return lrcIndex;  
		}
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//初始化数据库
		mbinder = new Mybinder();
		mp3infos = new GetMusicList().getExtraStorageMusicList();
		mhelper = new MyOpenHelper(Myapplication.getContext(), "musiclist.db", null, 1);
		db = mhelper.getWritableDatabase();
		//初始化上一次关闭程序时播放的最后的mp3 以便继续播放
		Cursor latelymusicCursor = db.query("latelyMusic", null, null, null, null, null, null);
		if(latelymusicCursor != null && latelymusicCursor.getCount() > 0){
			if(latelymusicCursor.moveToFirst()){
				Mp3Info mp3Info = new TranslaterOfData().musicCursorItem2mp3info(latelymusicCursor);
				mbinder.setMp3Info(mp3Info);
			}
		}
		latelymusicCursor.close();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
}
