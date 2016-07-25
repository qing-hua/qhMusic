package com.example.qinghua_music;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;
import modul.Mp3Info;
import utils.TranslaterOfData;

public class Online_frag_content extends Fragment{
	Online_subfrag_recomend online_subfrag_recomend ;
	Online_subfrag_result online_subfrag_result ;
	FragmentManager fragmentManager ;
	
	AutoCompleteTextView search_textview ;
	Button search_button ;
	
	List<Mp3Info> onlineMp3infos = null ;
	SQLiteDatabase db = null ;
	
	List<Mp3Info> onlineMp3infos_result ;
	
	public Online_frag_content(List<Mp3Info> onlineMp3infos , SQLiteDatabase db) {
		super();
		this.onlineMp3infos = onlineMp3infos;
		this.db = db ;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.online_frag_layout, container,false);
		search_textview = (AutoCompleteTextView)view.findViewById(R.id.search_textview);
		search_button = (Button)view.findViewById(R.id.search_button);
		fragmentManager = getActivity().getFragmentManager();
		intifragment();
		if(onlineMp3infos != null){
			initDB_onlineMusic();
		}
		search_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String searchKey = search_textview.getText().toString();
				onlineMp3infos_result = doSearch(searchKey) ;
				online_subfrag_result = new Online_subfrag_result(onlineMp3infos_result ,db);
				FragmentTransaction searchTransaction = fragmentManager.beginTransaction();
				searchTransaction.replace(R.id.search_content_fraglayout, online_subfrag_result);
				searchTransaction.addToBackStack(null);
				searchTransaction.commit();
			}
		}); 
		return view;
	}
	
	public void initDB_onlineMusic(){
		if(onlineMp3infos.size() != db.query("onlineMusic",null, null, null, null,null,null).getCount()){
			db.delete("onlineMusic", null, null) ;
			for(Mp3Info mp3Info :onlineMp3infos){
				ContentValues values = new TranslaterOfData().mp3info2values4onlineMusic(mp3Info);
				db.insert("onlineMusic", null, values);
			}
		}
	}
	
	public List<Mp3Info> doSearch(String searchKey){
		List<Mp3Info> infos = new ArrayList<Mp3Info>() ;
	//	Cursor cursor = db.rawQuery("select * from onlineMusic where music_name like ?", new String [] {"%" + searchKey + "%"});
		Cursor cursor = db.query("onlineMusic", null, "music_name like ?", new String [] {"%" + searchKey + "%"}, null, null, null) ;
		if(cursor != null && cursor.getCount() > 0){
			if(cursor.moveToFirst()){
				do{
					Mp3Info mp3Info = new TranslaterOfData().musicCursorItem2mp3info4onlineMusic(cursor);
					infos.add(mp3Info);
				}while(cursor.moveToNext());
			}
		}else{
			Toast.makeText(getActivity(), "Î´ËÑËÑµ½½á¹û", Toast.LENGTH_SHORT).show();
		}
		return infos ;
	}
	
	public void intifragment(){
		online_subfrag_recomend = new Online_subfrag_recomend(onlineMp3infos ,db);
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.search_content_fraglayout, online_subfrag_recomend);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
}
