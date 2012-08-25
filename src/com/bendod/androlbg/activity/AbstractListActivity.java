package com.bendod.androlbg.activity;

import com.bendod.androlbg.AndrOLBGApplication;

import android.content.res.Resources;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.bendod.androlbg.AndrOLBG;

public abstract class AbstractListActivity extends SherlockListActivity implements IAbstractActivity {

    protected AndrOLBGApplication app = null;
    protected Resources res = null;
    protected ActionBar actionBar = null;

	protected AbstractListActivity() {
    }

    final public void setTheme() {
        ActivityMixin.setTheme(this);
    }

    @Override
    public final void showToast(String text) {
        ActivityMixin.showToast(this, text);
    }

    @Override
    public final void showShortToast(String text) {
        ActivityMixin.showShortToast(this, text);
    }

    @Override
    public final void helpDialog(final String title, final String message) {
        ActivityMixin.helpDialog(this, title, message, null);
    }

    public final void helpDialog(final String title, final String message, final Drawable icon) {
        ActivityMixin.helpDialog(this, title, message, icon);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme();
		super.onCreate(savedInstanceState);

        // init
        res = this.getResources();
        app = (AndrOLBGApplication) this.getApplication();
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
    }
    
    @Override 
	public boolean onOptionsItemSelected(MenuItem item) { 
		switch (item.getItemId()) { 
			case android.R.id.home: // app icon in action bar clicked; go home 
			Intent intent = new Intent(this, AndrOLBG.class); 
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(intent); 
			return true; 
		default: 
			return super.onOptionsItemSelected(item); 
		} 
	}

}
