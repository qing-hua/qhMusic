package com.example.qinghua_music;

import java.io.File;
import java.util.List;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import modul.Mp3Info;
import user_defined.Constant;
import user_defined.Mp3itemAdapter;
import utils.Downloader;

public class Online_subfrag_result extends Fragment{
	ListView search_result_listview ;
	static List<Mp3Info> onlineMp3infos_result = null;
	SQLiteDatabase db ;
	public Online_subfrag_result(List<Mp3Info> onlineMp3infos_result,SQLiteDatabase db ) {
		super();
		this.onlineMp3infos_result = onlineMp3infos_result;
		this.db= db ;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.online_subfrag_result, container,false);
		search_result_listview = (ListView)view.findViewById(R.id.search_resutl_listview);
		if(onlineMp3infos_result != null){
			Mp3itemAdapter adapter = new Mp3itemAdapter(getActivity(), R.layout.mp3item_layout, onlineMp3infos_result);
			search_result_listview.setAdapter(adapter);
		}
		search_result_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Mp3Info mp3Info = onlineMp3infos_result.get(position);
				File mp3file = new File(mp3Info.getPath());
				if(! mp3file.exists()){
					new Downloader().downloadMp3("qhMusic/", mp3Info.getName(), Constant.DOWNLOADMP3_URL + mp3Info.getName(),mp3Info ,db);
					new Downloader().downloadLrc(mp3Info, Constant.DOWNLOADMP3_URL + mp3Info.getName());
					((MainActivity)getActivity()).doAddData2DownloadMp3Infos(mp3Info);
					Toast.makeText(getActivity(), "¿ªÊ¼ÏÂÔØ",Toast.LENGTH_SHORT).show();
				}else if(mp3file.exists() && mp3file.length() > 0){
					((MainActivity)getActivity()).doPlay4subfrag(mp3Info);
				}
			}
		});
		return view;
	}
}
