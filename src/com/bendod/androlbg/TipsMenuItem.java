package com.bendod.androlbg;

import android.graphics.drawable.Drawable;

public class TipsMenuItem {

	public int title;
	public int tipId;
	public Drawable imgSrc;
	public String intent;
	
	public TipsMenuItem(){
		// TODO Auto-generated constructor stub
	}

	public TipsMenuItem(int title, int tipId, Drawable imgSrc, String intent){
		this.title = title;
		this.tipId = tipId;
		this.imgSrc = imgSrc;
		this.intent = intent;
	}

	@Override
	public String toString(){
		return AndrOLBGApplication.getInstance().getResources().getString(title);
	}
	
}
