package user_defined;

import java.io.File;
import java.util.List;

import com.example.qinghua_music.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import modul.Mp3Info;

public class DownloaditemAdapter extends ArrayAdapter<Mp3Info>{
	int resource ;
	Context mContext ;
	List<Mp3Info> downloadMp3infos ;
	Runnable runnable ;
  
	public DownloaditemAdapter(Context context, int resource, List<Mp3Info> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.resource = resource;
		this.mContext = context ;
		this.downloadMp3infos = objects ;
	}
   
   	@Override
   	public View getView(int position, View convertView, ViewGroup parent) {
   		// TODO Auto-generated method stub
   		View view = null ;
   		ViewHolder viewHolder ;
   		Mp3Info mp3Info = getItem(position);
   	    Handler handler = new Handler();
   		if(convertView == null){
   			view = LayoutInflater.from(mContext).inflate(resource, null);
   			viewHolder = new ViewHolder();
   			viewHolder.name_of_download = (TextView)view.findViewById(R.id.name_of_dawnload);
   			viewHolder.value_of_progress = (TextView)view.findViewById(R.id.value_of_progress);
   			viewHolder.progress_of_downloading = (ProgressBar)view.findViewById(R.id.progress_of_downloading);
   			view.setTag(viewHolder);
   		}else{
   			view = convertView ;
   			viewHolder = (ViewHolder) view.getTag();
   		}
   		viewHolder.name_of_download.setText(mp3Info.getName());
   		//构造getRunnable函数是为了将对应的参数传递到runnable中
   		Runnable r = getRunnable(handler, mp3Info , viewHolder);
   		//刷新 下载的进度
   		handler.post(r);
   		return view;
   	}
   
   public Runnable getRunnable(final Handler handler ,final Mp3Info mp3Info , final ViewHolder viewHolder){ 
	   runnable = new Runnable() {
  			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//通过要下载的文件的大小和已经下载的大小得出下载进度（已下载的大小是通过路径找到对应文件查看其大小）
				//這里更新进度条进度和下载文件的线程是分开的
				File file = new File(mp3Info.getPath());
		   		int length = (int) file.length();
		   		int totalLength = mp3Info.getSize();
	   	   		viewHolder.value_of_progress.setText(length  * 100 / totalLength + "%");
	   	   		viewHolder.progress_of_downloading.setMax(100);
	   	   		viewHolder.progress_of_downloading.setProgress(length * 100 / totalLength);
	   	   		handler.postDelayed(runnable, 500);
			}
  		};
  	 	return runnable ;
   }
   public class ViewHolder{
   		TextView name_of_download ;
   		TextView value_of_progress ;
   		ProgressBar progress_of_downloading ;
   	}
}
