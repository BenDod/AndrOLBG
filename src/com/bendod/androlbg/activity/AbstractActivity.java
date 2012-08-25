package com.bendod.androlbg.activity;

import com.bendod.androlbg.AndrOLBG;
import com.bendod.androlbg.AndrOLBGApplication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.EditText;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public abstract class AbstractActivity extends SherlockActivity implements IAbstractActivity {

    protected AndrOLBGApplication app = null;
    protected Resources res = null;
    protected ActionBar actionBar = null;

    protected AbstractActivity() {
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
        super.onCreate(savedInstanceState);

        // init
        res = this.getResources();
        app = (AndrOLBGApplication) this.getApplication();
        actionBar = getSupportActionBar();
        if(actionBar != null){
        	actionBar.setHomeButtonEnabled(true);
        }
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
    
    /**
     * insert text into the EditText at the current cursor position
     *
     * @param editText
     * @param insertText
     * @param moveCursor
     *            place the cursor after the inserted text
     */
    public static void insertAtPosition(final EditText editText, final String insertText, final boolean moveCursor) {
        int selectionStart = editText.getSelectionStart();
        int selectionEnd = editText.getSelectionEnd();
        int start = Math.min(selectionStart, selectionEnd);
        int end = Math.max(selectionStart, selectionEnd);

        final String content = editText.getText().toString();
        String completeText;
        if (start > 0 && !Character.isWhitespace(content.charAt(start - 1))) {
            completeText = " " + insertText;
        } else {
            completeText = insertText;
        }

        editText.getText().replace(start, end, completeText);
        int newCursor = moveCursor ? start + completeText.length() : start;
        editText.setSelection(newCursor, newCursor);
    }

}
