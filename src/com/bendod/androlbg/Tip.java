package com.bendod.androlbg;

import com.bendod.androlbg.utils.Utils;

public class Tip{

	public String sport;
	public String subSport;
	public String feedTable;
	public String eventId;
	public int marketId;
	public String selection;
	public String comment;
	public int stake;
	public String betType;
	public boolean nap;
	public boolean nb;
	public String price;
	public String course;
	
	public Tip() {
		this.marketId = -1;
	}

	public String validateTip(String stakeStr)
	{
		if(Utils.isBlank(this.eventId)) {
			return "Pick an Event first";
		}
		if(this.marketId == -1) {
			return "Pick a Market first";
		}
		if(Utils.isBlank(this.selection)) {
			return "Make a Selection";
		}
		if(Utils.isBlank(stakeStr)) {
			return "Enter a Stake";
		}
		this.stake = Integer.parseInt(stakeStr);
		if(this.stake < 50 || this.stake > 500) {
			return "Stake between 50 and 500";
		}
		if(Utils.isBlank(this.price)) {
			if(this.subSport.equals("Horse Racing")){
				return "Select Odds";
			}else{
				this.price = "Take Price";
			}
		}
		return null;
	}
		
}
