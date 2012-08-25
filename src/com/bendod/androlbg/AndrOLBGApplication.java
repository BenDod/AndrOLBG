package com.bendod.androlbg;

import android.app.Application;

public class AndrOLBGApplication extends Application {

	public boolean firstRun = true; // AndrOLBG is just launched
	public boolean showLoginToast = true; //login toast shown just once.
    private static AndrOLBGApplication instance = null;
	
	public AndrOLBGApplication() {
        instance = this;
    }

    public static AndrOLBGApplication getInstance() {
        return instance;
    }

}
