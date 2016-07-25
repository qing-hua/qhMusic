package playerservice;

import java.util.List;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import modul.Mp3Info;
import user_defined.LrcContentView;

public interface MusicOperator {

	public void doStart(SeekBar seekBar ,Button music_start ,String path);
	
	public void doPause();
	
	public void doPrev(SeekBar seekBar ,Button music_start);
	
	public void doNext(SeekBar seekBar ,Button music_start);
	
	public void doSetDefault();
	
	public void doInit(SeekBar seekBar ,Button music_start ,String path);
	
	public void doLoad(SeekBar seekBar , Button music_start);
	
	public void doSaveLatalyMusic() ;
	
	public void doInitMp3info(Mp3Info mp3Info);
	
	public void setLrcContentView(TextView lrcContentview_music_name ,TextView lrcContentview_music_singer ,LrcContentView lrcContentView);
	
	public void doInitLrcContent();
	
}
