package utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityCollecter {
	
	public static List<Activity> activities = new ArrayList<Activity>() ;
	
	public static void addActivity(Activity activity){
		activities.add(activity);
	}
	
	public static void removeActivity(Activity activity){
		activities.remove(activity);
	}
	
	public static void finishALL(){
		for(Activity activity : activities){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}
}
