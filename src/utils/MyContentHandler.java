package utils;

import java.net.ContentHandler;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import modul.Mp3Info;
import user_defined.Constant;

public class MyContentHandler extends DefaultHandler{
	
	List<Mp3Info> onlineMp3infos = null ;
	String tagName = null;
	Mp3Info mp3Info = null ;
	int id = 0 ;
/*	String mp3Name = null ;
	String singer = null ;
	int time = 0 ;
	String lrcName = null ; */
	
	public MyContentHandler(List<Mp3Info> onlineMp3infos) {
		super();
		this.onlineMp3infos = onlineMp3infos;
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.endDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		tagName = localName ;
		if(localName.equals("resource")){
			mp3Info = new Mp3Info();
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if(qName.equals("resource")){
			mp3Info.setPath(Constant.PATH + mp3Info.getName());
			mp3Info.setId(id);
			int time = mp3Info.getMsTime() ;
			int second = time % 60000 /1000 ;
			int minute = time / 60000 ;
			String musicTime = ":" ;
			if(minute == 0){
				musicTime = "00" + musicTime ;
			}
			else if(0 < minute && minute < 10){
				musicTime = "0" + minute + musicTime ;
			}
			else {
				musicTime = minute + musicTime ;
			}
			if(second < 10){
				musicTime = musicTime + "0" + second ;
			}
			else {
				musicTime = musicTime + second ;
			}
			mp3Info.setTime(musicTime);
			onlineMp3infos.add(mp3Info);
			id ++ ;
		}
		tagName = "" ;
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		String temp = new String(ch, start, length);
		if(tagName.equals("mp3Name")){
			mp3Info.setName(temp);
		}
		else if(tagName.equals("singer")){
			mp3Info.setSinger(temp);
		}
		else if(tagName.equals("time")){
			mp3Info.setMsTime(Integer.parseInt(temp));
		}
		else if(tagName.equals("size")){
			mp3Info.setSize(Integer.parseInt(temp));
		}
	}
	
}
