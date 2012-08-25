package com.bendod.androlbg;

import android.util.Pair;

public class LabelValuePair extends Pair<String, String> {

	public LabelValuePair(String first, String second) {
		super(first, second);
	}
	
	@Override
	public String toString(){
		return first;		
	}
	
	public String getValue(){
		return second;
	}
}
