package modul;

import java.io.Serializable;

public class Mp3Info implements Serializable{
	
	int id = 0 ;
	int ablumId = 0 ;
	String name = null ;
	String path = null ;
	String time = null ;
	String singer = null ;
	int msTime = 0 ;
	int size = 0 ;
	int position = -1 ;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public int getMsTime() {
		return msTime;
	}
	public void setMsTime(int msTime) {
		this.msTime = msTime;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getAblumId() {
		return ablumId;
	}
	public void setAblumId(int ablumId) {
		this.ablumId = ablumId;
	}
	
	
}
