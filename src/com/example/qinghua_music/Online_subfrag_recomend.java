package com.example.qinghua_music;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import modul.Mp3Info;
import user_defined.Constant;
import user_defined.Mp3itemAdapter;
import utils.Downloader;

public class Online_subfrag_recomend extends Fragment{
	ListView search_recomend_listview ;
	
	List<Mp3Info> onlineMp3infos = null ;
	List<Mp3Info> onlineMp3infos_recomend = new ArrayList<Mp3Info>() ;
	
	SQLiteDatabase db ;

	
	public Online_subfrag_recomend(List<Mp3Info> onlineMp3infos ,SQLiteDatabase db) {
		super();
		this.onlineMp3infos = onlineMp3infos;
		this.db = db ;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.online_subfrag_recomend, container,false);
		search_recomend_listview = (ListView)view.findViewById(R.id.search_recomend_listview);
		if(onlineMp3infos != null){
			initOnlineMp3infos_recomend();
			Mp3itemAdapter adapter = new Mp3itemAdapter(getActivity(), R.layout.mp3item_layout, onlineMp3infos_recomend);
			search_recomend_listview.setAdapter(adapter);
		}
		search_recomend_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Mp3Info mp3Info = onlineMp3infos_recomend.get(position);
				File mp3file = new File(mp3Info.getPath());
				if(! mp3file.exists()){
					new Downloader().downloadMp3("qhMusic/", mp3Info.getName(), Constant.DOWNLOADMP3_URL + mp3Info.getName(),mp3Info ,db);
					new Downloader().downloadLrc(mp3Info, Constant.DOWNLOADMP3_URL + mp3Info.getName());
					((MainActivity)getActivity()).doAddData2DownloadMp3Infos(mp3Info);
					Toast.makeText(getActivity(), "开始下载",Toast.LENGTH_SHORT).show();
				}else if(mp3file.exists() && mp3file.length() > 0){
					((MainActivity)getActivity()).doPlay4subfrag(mp3Info);
				}
			}
		});
		return view;
	}
	//默认推荐歌曲列表未从网络上查询到的前三首
	public void initOnlineMp3infos_recomend(){
		if(onlineMp3infos.size() > 2){
			onlineMp3infos_recomend.add(onlineMp3infos.get(0));
			onlineMp3infos_recomend.add(onlineMp3infos.get(1));
			onlineMp3infos_recomend.add(onlineMp3infos.get(2));
		}else{
			onlineMp3infos_recomend = onlineMp3infos ;
		}
	}
}
