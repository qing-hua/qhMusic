package user_defined;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import modul.LrcContentInfo;

public class LrcContentView extends TextView{
	float width ;
	float height ;
	Paint currentPaint ;
	Paint notCurrentPaint ;
	float textHeight = 70 ;
	float textSize = 50 ;
	int index = 0 ;
	
	static List<LrcContentInfo> lrcContentInfos = null;
	
	public void setLrcContentInfos(List<LrcContentInfo> lrcContentInfos){
		this.lrcContentInfos = null ;
		this.lrcContentInfos = lrcContentInfos ;
	}
	
	public void setIndex(int index){
		this.index = index ;
	}
	
	public LrcContentView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public LrcContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public void init(){
		currentPaint = new Paint();
		currentPaint.setAntiAlias(true);
		currentPaint.setTextAlign(Paint.Align.CENTER);
		
		notCurrentPaint = new Paint();
		notCurrentPaint.setAntiAlias(true);
		notCurrentPaint.setTextAlign(Paint.Align.CENTER);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(canvas == null) {  
            return;  
        }  
		if(lrcContentInfos != null ){
			
			currentPaint.setColor(Color.YELLOW);
			currentPaint.setTextSize(65);
			currentPaint.setTypeface(Typeface.SERIF);
			
			notCurrentPaint.setColor(Color.GRAY);
			notCurrentPaint.setTextSize(textSize);
			notCurrentPaint.setTypeface(Typeface.DEFAULT);
			
			try {
				setText("");
				canvas.drawText(lrcContentInfos.get(index).getLrcStr(), width / 2, height / 2 , currentPaint);
				
				float tempPrev =  height / 2 ;
				for(int i = index - 1 ; i >=0 ;i --){
					tempPrev = tempPrev - textHeight ;
					canvas.drawText(lrcContentInfos.get(i).getLrcStr(), width / 2, tempPrev, notCurrentPaint);
				}
				
				float tempNext = height / 2 ;
				for(int i = index + 1 ; i < lrcContentInfos.size() ; i ++){
					tempNext = tempNext + textHeight ;
					canvas.drawText(lrcContentInfos.get(i).getLrcStr(), width / 2, tempNext, notCurrentPaint);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				setText("没有找到歌词文件 ，赶紧去下载吧");
			}
		} else {
			setText("没有找到歌词文件 ，赶紧去下载吧");
		}
		
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w ;
		this.height = h ;
	}

}
