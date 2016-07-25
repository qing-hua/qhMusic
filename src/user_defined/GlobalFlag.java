package user_defined;

import java.util.List;

import android.widget.Button;
import android.widget.SeekBar;
import modul.Mp3Info;
import playerservice.MusicOperator;

public class GlobalFlag {
	
	public static boolean ISPLAYING = false ;
	
	public static MusicOperator mBinder = null ;
	
	public static SeekBar seekBar = null ;
	
	public static Button music_start = null;
	
	public static final int ORDER_PLAYING = 1 ;
	
	public static final int RANDOM_PLAYING = 2 ;
	
	public static final int LOOPER_PLAYING = 3 ;
	
	public static int TYPE_PLAYING = ORDER_PLAYING ;
	
	public static boolean SWITCH_OFF = false ;
	
	public static boolean SWITCH_ON = true ;
	
	public static boolean SWITCH_STATE = SWITCH_OFF ;
	
	public static List<Mp3Info> historyMp3infos = null ;
	
	public static int position4histroyMusic = 0 ;
	
	public static final int download_success = 0 ;
	
	public static final int download_fail = 1 ;
	
//	public static int TYPE_PLAYING = RANDOM_PLAYING ;
}
