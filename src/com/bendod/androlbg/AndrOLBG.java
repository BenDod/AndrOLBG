package com.bendod.androlbg;

import java.io.IOException;
import java.net.URISyntaxException;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.bendod.androlbg.enumerations.StatusCode;
import com.bendod.androlbg.activity.AbstractActivity;
import com.bendod.androlbg.connector.Login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class AndrOLBG extends AbstractActivity {

	private int countUnsettledTips = 0;
	private int countVirtualMoney = 0;
    private boolean initialized = false;
	
	private Handler updateUserInfoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            TextView userInfoView = (TextView) findViewById(R.id.user_info);
            TextView virtualMoneyView = (TextView) findViewById(R.id.virtual_money);
            TextView countBubble = (TextView) findViewById(R.id.tips_count); 
            		
            StringBuilder userInfo = new StringBuilder("OLBG.com · ");
            if (Login.isActualLoginStatus()) {
                userInfo.append(Login.getActualUserName()).append(" · ");
                countUnsettledTips = Login.getActualUnsettledTips();
            	if (countUnsettledTips >= 0) {
                	if (countUnsettledTips == 0) {
                        countBubble.setVisibility(View.GONE);
                    } else {
                        countBubble.setText(Integer.toString(countUnsettledTips));
                        countBubble.bringToFront();
                        countBubble.setVisibility(View.VISIBLE);
                    }
                }                
            }
            userInfo.append(Login.getActualStatus());
            
            userInfoView.setText(userInfo.toString());
            
        	countVirtualMoney = Login.getActualVirtualMoney();
        	if (countVirtualMoney >= 0) {
        		userInfo.delete(0, userInfo.length());
        		userInfo.append(Integer.toString(countVirtualMoney)).append(" ");
        		userInfo.append(res.getString(R.string.virtual_money));
        		virtualMoneyView.setText(userInfo.toString());
        		virtualMoneyView.setVisibility(View.VISIBLE);
        	}else{
        		virtualMoneyView.setVisibility(View.INVISIBLE);
        	}
            
        }
    };
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
		setTheme(R.style.olbg_main);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        init();
    }
	
	@Override
    public void onResume() {
        super.onResume();
        updateUserInfoHandler.sendEmptyMessage(-1);
        init();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
		final Intent intent;
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	intent = new Intent(this, AboutActivity.class); 
				startActivity(intent);
	            return true;
			case R.id.settings:
				intent = new Intent(this, SettingsActivity.class); 
				startActivity(intent);
	            return true;
			case R.id.browser:
				intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.olbg.com/"));
				startActivity(intent);
			    return true;
			default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        
        Settings.getLogin();

        if (app.firstRun) {
            (new firstLogin()).start();
        }

        final View makeTip = findViewById(R.id.make_tip);
        makeTip.setClickable(true);
        makeTip.setOnClickListener(new OnClickListener() {
            //@Override
            public void onClick(View v) {
            	olbgMakeTip(v);
            }
        });

        final View tipHistory = findViewById(R.id.history);
        tipHistory.setClickable(true);
        tipHistory.setOnClickListener(new OnClickListener() {
            //@Override
            public void onClick(View v) {
            	olbgTipHistory(v);
            }
        });

        final View currentTips = findViewById(R.id.current);
        currentTips.setClickable(true);
        currentTips.setOnClickListener(new OnClickListener() {
            //@Override
            public void onClick(View v) {
            	olbgCurrentTips(v);
            }
        });

    }
	
    /**
     * @param view
     *            unused here but needed since this method is referenced from XML layout
     */
    public void olbgMakeTip(View view) {
		findViewById(R.id.make_tip).setPressed(true);
		final Intent intent = new Intent(this, TipsMenuActivity.class);
		startActivity(intent); 
	}

    /**
     * @param view
     *            unused here but needed since this method is referenced from XML layout
     */
    public void olbgTipHistory(View view) {
		findViewById(R.id.history).setPressed(true);
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.olbg.com/tipster/history.php?id="+Integer.toString(Login.getActualUserId())));
		startActivity(intent); 
	}

    /**
     * @param view
     *            unused here but needed since this method is referenced from XML layout
     */
    public void olbgCurrentTips(View view) {
		findViewById(R.id.current).setPressed(true);
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.olbg.com/members/currenttips.php"));
		startActivity(intent); 
	}

    private class firstLogin extends Thread {

        @Override
        public void run() {
            if (app == null) {
                return;
            }

            // login
            StatusCode status = null;
			try {
				status = Login.login();
			} catch (IOException e) {
			} catch (URISyntaxException e) {
			}

            if (status == StatusCode.NO_ERROR) {
                app.firstRun = false;
                //Login.detectGcCustomDate();
                updateUserInfoHandler.sendEmptyMessage(-1);
            }

            if (app.showLoginToast) {
                //firstLoginHandler.sendMessage(firstLoginHandler.obtainMessage(0, status));
                app.showLoginToast = false;

                // invoke settings activity to insert login details
                if (status == StatusCode.NO_LOGIN_INFO_STORED) {
                	SettingsActivity.startActivity(AndrOLBG.this);
                }
            }
        }
    }

}
