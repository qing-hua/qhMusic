package com.example.qinghua_music;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import playerservice.MusicOperator;

public class LocalFragment extends Fragment{
	LinearLayout all_music ;
	LinearLayout my_music ;
	LinearLayout history_music ;
	TextView numb_all_music ;
	TextView numb_my_music ;
	TextView numb_history_music ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.mymusic_frag_layout, container ,false);
		all_music = (LinearLayout)view.findViewById(R.id.all_music);
		my_music = (LinearLayout)view.findViewById(R.id.my_music);
		history_music = (LinearLayout)view.findViewById(R.id.history_music);
		numb_all_music = (TextView)view.findViewById(R.id.numb_all_music);
		numb_my_music = (TextView)view.findViewById(R.id.numb_my_music);
		numb_history_music = (TextView)view.findViewById(R.id.numb_history_music);
		all_music.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ToAll_music_Activity.class);
				getActivity().startActivity(intent);
			}
		});
		my_music.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ToMy_music_Activity.class);
				getActivity().startActivity(intent);
			}
		});
		history_music.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ToHistory_music_Activity.class);
				getActivity().startActivity(intent);
			}
		});
		return view ;
	}
}
