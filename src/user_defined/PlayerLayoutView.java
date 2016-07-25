package user_defined;

import com.example.qinghua_music.MainActivity;
import com.example.qinghua_music.Playing_Activity;
import com.example.qinghua_music.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import database.MyOpenHelper;
import modul.Mp3Info;
import playerservice.MusicOperator;
import playerservice.MyPllayerService;
import utils.TranslaterOfData;

public class PlayerLayoutView extends LinearLayout{
	Context mContext ;
	LinearLayout mp3Layout ;
	ImageView img_singer ;
	Button music_previous ;
	Button music_start ;
	Button music_next ; 
	SeekBar playing_progress ;
	SeekBar seekBar ;
	String path = null ;
	MusicOperator mBinder = null;
	MyOpenHelper mHelper = null ;
	SQLiteDatabase db = null ;
	
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
	
	public PlayerLayoutView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.mp3player_layout, this);
		mContext = context;
		mp3Layout = (LinearLayout)findViewById(R.id.mp3layout);
		img_singer = (ImageView)findViewById(R.id.img_singer);
		music_previous = (Button)findViewById(R.id.music_previous);
		music_start = (Button)findViewById(R.id.music_start);
		music_next = (Button)findViewById(R.id.music_next);
		playing_progress = (SeekBar)findViewById(R.id.playing_progress);
		mHelper = new MyOpenHelper(context, "musiclist.db", null, 1);
		db = mHelper.getWritableDatabase();
		if(db != null ){
			Cursor latelymusicCursor = db.query("latelyMusic", null, null, null, null, null, null);
				if(latelymusicCursor.moveToFirst()){
					Mp3Info mp3Info = new TranslaterOfData().musicCursorItem2mp3info(latelymusicCursor);
					path = mp3Info.getPath();
				}
			latelymusicCursor.close();
		}
		mp3Layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, Playing_Activity.class);
				context.startActivity(intent);
			}
		});
		
		Intent intent = new Intent(context, MyPllayerService.class);
		context.bindService(intent, serviceConnection,context.BIND_AUTO_CREATE);
		
		music_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mBinder != null && !GlobalFlag.ISPLAYING){
					GlobalFlag.ISPLAYING = true ;
					music_start.setBackgroundResource(R.drawable.ic_pause);
					mBinder.doStart(playing_progress ,music_start ,path);
				}	
				else if(mBinder != null && GlobalFlag.ISPLAYING){
					GlobalFlag.ISPLAYING = false ;
					music_start.setBackgroundResource(R.drawable.ic_start);
					mBinder.doPause();
				}
			}
		});
		
		music_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				music_start.setBackgroundResource(R.drawable.ic_pause);
				GlobalFlag.ISPLAYING = true ;
				mBinder.doNext(playing_progress,music_start);
			}
		});
		
		music_previous.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				music_start.setBackgroundResource(R.drawable.ic_pause);
				GlobalFlag.ISPLAYING = true ;
				mBinder.doPrev(playing_progress,music_start);
			}
		}); 
	}
	
	public void doUnbinding4palyerview(){
		mContext.unbindService(serviceConnection);
	}
}
