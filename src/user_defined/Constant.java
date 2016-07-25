package user_defined;

import android.os.Environment;

public class Constant {
	public static final String MUSTICLIST_URL = "http://192.168.1.100:8080/qhMusic/musicList.xml" ;
	
	public static final String DOWNLOADMP3_URL = "http://192.168.1.100:8080/qhMusic/" ;
	
	public static final String PATH = Environment.getExternalStorageDirectory() + "/qhMusic/";
}
