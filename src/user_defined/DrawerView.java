package user_defined;

import java.util.ArrayList;
import java.util.List;

import com.example.qinghua_music.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import database.MyOpenHelper;
import playerservice.MusicOperator;
import playerservice.MyPllayerService;
import utils.ActivityCollecter;

public class DrawerView extends LinearLayout{
	ImageButton typeofplaying_button ;
	CheckBox sliping_changemusic ;
	Spinner select_time_spinner ;
	LinearLayout menu_about;
	LinearLayout menu_exit;
	List<String> selectable_time ;
	MyOpenHelper mHelper ;
	SQLiteDatabase db ;
	Context mContext ;
	//定时退出
	public static boolean isDelayStopOn = false ;
	public static int delayTime = 0 ;
	Handler handler = new Handler() ;
	Runnable delayTime2stopRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(isDelayStopOn){
				ActivityCollecter.finishALL();
			}
		}
	};
	
	public DrawerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.drawer_view_layout, this);
		mContext = context ;
		typeofplaying_button = (ImageButton)findViewById(R.id.typeofplaying_switch);
		sliping_changemusic = (CheckBox)findViewById(R.id.sliping_changemusic);
		select_time_spinner = (Spinner)findViewById(R.id.select_time_spinner);
		menu_about = (LinearLayout)findViewById(R.id.menu_about);
		menu_exit = (LinearLayout)findViewById(R.id.menu_exit);
		
		mHelper = new MyOpenHelper(context, "musiclist.db", null, 1);
		db = mHelper.getWritableDatabase();
		
		//初始化typeofplaying_button
		initTypeofplaying_button_background();
		
		//设置定时关机的时间
		selectable_time = getSelectableTime();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, selectable_time);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		select_time_spinner.setAdapter(adapter);
		select_time_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				switch(position){
					case 0 :
						isDelayStopOn = false ;
						delayTime = 0 ;
						break ;
					case 1 :
						isDelayStopOn = true ;
						delayTime = 30 * 60 * 1000 ;
						break ;
					case 2 :
						isDelayStopOn = true ;
						delayTime = 60 * 60 * 1000 ;
						break ;
					case 3 :
						isDelayStopOn = true ;
						delayTime = 90 * 60 * 1000 ;
						break ;
					case 4 : 
						isDelayStopOn = true ;
						delayTime = 120 * 60 * 1000 ;
						break ;
					case 5 :
						isDelayStopOn = true ;
						delayTime = 20 * 1000 ;
						break ;
				}
				handler.postDelayed(delayTime2stopRunnable, delayTime);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	
		//切换播放模式
		typeofplaying_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(GlobalFlag.TYPE_PLAYING == GlobalFlag.ORDER_PLAYING){
					GlobalFlag.TYPE_PLAYING = GlobalFlag.RANDOM_PLAYING ;
					typeofplaying_button.setBackgroundResource(R.drawable.ic_random_playing);
				}
				else if(GlobalFlag.TYPE_PLAYING == GlobalFlag.RANDOM_PLAYING){
					GlobalFlag.TYPE_PLAYING = GlobalFlag.LOOPER_PLAYING;
					typeofplaying_button.setBackgroundResource(R.drawable.ic_looper_playing);
				}
				else if(GlobalFlag.TYPE_PLAYING == GlobalFlag.LOOPER_PLAYING){
					GlobalFlag.TYPE_PLAYING = GlobalFlag.ORDER_PLAYING ;
					typeofplaying_button.setBackgroundResource(R.drawable.ic_order_palying);
				}
			//将播放的模式记录下来
				ContentValues values = new ContentValues();
				values.put("typeOfPlaying", GlobalFlag.TYPE_PLAYING);
				db.delete("stateOfPlayingType", null, null);
				db.insert("stateOfPlayingType", null, values);
			}
		});
		
		//开关侧屏切歌，默认是关闭，每次重新启动程序需要重新开启
		sliping_changemusic.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					GlobalFlag.SWITCH_STATE = GlobalFlag.SWITCH_ON;
					sliping_changemusic.setText("开");
				}else{
					GlobalFlag.SWITCH_STATE = GlobalFlag.SWITCH_OFF;
					sliping_changemusic.setText("关");
				}
			}
		});
		
		//设置关于信息的点击事件
		menu_about.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new Builder(mContext);
				builder.setTitle("about");
				builder.setMessage(R.string.about);
				builder.setCancelable(true);
				builder.create().show();
			}
		});
		
		//设置退出的点击事件
		menu_exit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ActivityCollecter.finishALL();
			}
		});
	}

	
	public List<String> getSelectableTime(){
		List<String> list = new ArrayList<String>() ;
		list.add("关闭");
		list.add("30");
		list.add("60");
		list.add("90");
		list.add("120");
		list.add("测试");
		return list ;
	}
	public void initTypeofplaying_button_background(){
		if(GlobalFlag.TYPE_PLAYING == GlobalFlag.ORDER_PLAYING){
			typeofplaying_button.setBackgroundResource(R.drawable.ic_order_palying);
		}
		else if(GlobalFlag.TYPE_PLAYING == GlobalFlag.RANDOM_PLAYING){
			typeofplaying_button.setBackgroundResource(R.drawable.ic_random_playing);
		}
		else if(GlobalFlag.TYPE_PLAYING == GlobalFlag.LOOPER_PLAYING){
			typeofplaying_button.setBackgroundResource(R.drawable.ic_looper_playing);
		}
	}
}
