package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import modul.Mp3Info;

public class FileOperater {
	
	String SDCARD_PATH ;
	
	public FileOperater() {
		super();
		SDCARD_PATH = Environment.getExternalStorageDirectory() + "/" ;
	}

	public File createDir(String dirName){
		File dir = new File(SDCARD_PATH + dirName);
		dir.mkdir() ;
		return dir ;
	}
	
	public File createFile(String fileName){
		File file = new File(SDCARD_PATH + fileName) ;
		try {
			file.createNewFile() ;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file ;
	}
	
	public boolean isFileExist(String fileName){
		File file = new File(fileName);
		return file.exists() ;
	}
	
	public File writeFromInput2SDcard(String dirName , String fileName , InputStream input){
		createDir(dirName);
		int length = - 1;
		File file = createFile(dirName + fileName);
		OutputStream output = null ;
		byte [] buffer = new byte [4 * 1024] ;
		try {
			output = new FileOutputStream(file);
			while((length = input.read(buffer)) != -1){
				output.write(buffer, 0 ,length);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if(output != null){
					output.flush();
					output.close();
				}
				if(input != null){
					input.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return file ;
	}
	public List<Mp3Info> getMyMusic(){
		File file = new File(SDCARD_PATH + "qhMusic/");
		File[] files = file.listFiles();
		for(File f : files){
			
		}
		return null;
	}
}
