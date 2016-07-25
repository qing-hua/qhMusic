package com.example.qinghua_music;

import java.io.File;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import modul.Mp3Info;
import user_defined.DownloaditemAdapter;

public class DownloadFragment extends Fragment{
	List<Mp3Info> downloadingMp3infos ;
	DownloaditemAdapter adapter ;
	
	public DownloadFragment(List<Mp3Info> downloadingMp3infos) {
		super();
		this.downloadingMp3infos = downloadingMp3infos;
	}
	//刷新listview ；
	Handler handler = new Handler();
	Runnable r = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			adapter.notifyDataSetChanged();
			handler.postDelayed(r, 500);
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.download_farg_layout, container, false);
		ListView download_listview = (ListView)view.findViewById(R.id.download_listview);
		if(downloadingMp3infos != null){
			adapter = new DownloaditemAdapter(getActivity(), R.layout.download_item_layout, downloadingMp3infos);
			handler.post(r);
			download_listview.setAdapter(adapter);
			download_listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					Mp3Info mp3Info = downloadingMp3infos.get(position);
					File file = new File(mp3Info.getPath());
					if(file.exists() && file.length() > 0){
						((MainActivity)getActivity()).doPlay4subfrag(mp3Info);
					}
				}
			});
			download_listview.setOnItemLongClickListener(new OnItemLongClickListener() {

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
					// TODO Auto-generated method stub
					final Mp3Info mp3Info = downloadingMp3infos.get(position);
					AlertDialog.Builder builder = new Builder(getActivity());
					builder.setTitle("提示");
					builder.setMessage("是否删除该下载文件");
					builder.setNegativeButton("NO", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					builder.setPositiveButton("OK", new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							new File(mp3Info.getPath()).delete();
							downloadingMp3infos.remove(position);
						}
					});
					builder.create().show();
					return true;
				}
			});
		}
		return view;
	}
}
