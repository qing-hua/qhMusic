package user_defined;

import java.util.List;

import com.example.qinghua_music.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import modul.Mp3Info;

public class Mp3itemAdapter extends ArrayAdapter<Mp3Info>{
	int resource ;
	public Mp3itemAdapter(Context context, int resource, List<Mp3Info> objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.resource = resource ;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Mp3Info mp3Info = getItem(position);
		View view ;
		ViewHolder viewHolder ;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resource, null);
			viewHolder = new ViewHolder();
			viewHolder.mp3item_name = (TextView)view.findViewById(R.id.mp3item_name);
			viewHolder.mp3iteme_singer = (TextView)view.findViewById(R.id.mp3item_singer);
			viewHolder.mp3item_time = (TextView)view.findViewById(R.id.mp3itme_time);
			view.setTag(viewHolder);
		}else{
			view = convertView ;
			viewHolder = (ViewHolder)view.getTag();
		}
		viewHolder.mp3item_name.setText(mp3Info.getName());
		viewHolder.mp3iteme_singer.setText(mp3Info.getSinger());
		viewHolder.mp3item_time.setText(mp3Info.getTime());
		return view ;
	}
	
	public class ViewHolder{
		
		TextView mp3item_name ;
		
		TextView mp3iteme_singer ;
		
		TextView mp3item_time ;
	}
}
