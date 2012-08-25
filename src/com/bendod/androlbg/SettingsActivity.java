package com.bendod.androlbg;

import java.io.IOException;
import java.net.URISyntaxException;

import com.bendod.androlbg.activity.AbstractActivity;
import com.bendod.androlbg.connector.Login;
import com.bendod.androlbg.enumerations.StatusCode;
import com.bendod.androlbg.utils.Utils;
import com.bendod.androlbg.twitter.TwitterAuthorizationActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.*;

public class SettingsActivity extends AbstractActivity {

	private ProgressDialog loginDialog = null;
	private Handler logInHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            try {
                if (loginDialog != null && loginDialog.isShowing()) {
                    loginDialog.dismiss();
                }

                if (msg.obj == null || (msg.obj instanceof Drawable)) {
                	helpDialog(res.getString(R.string.init_login_popup), res.getString(R.string.init_login_popup_ok),
                            (Drawable) msg.obj);
                } else {
                    helpDialog(res.getString(R.string.init_login_popup),
                            res.getString(R.string.init_login_popup_failed_reason) + " " +
                                    ((StatusCode) msg.obj).getErrorString(res) + ".");
                }
            } catch (Exception e) {
                showToast(res.getString(R.string.err_login_failed));
            }
            if (loginDialog != null && loginDialog.isShowing()) {
                loginDialog.dismiss();
            }
            init();
        }
    };
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme();
		super.onCreate(savedInstanceState);

        // init

        setContentView(R.layout.init);
        setTitle(R.string.settings);

        init();
    }
	
	public void init() {

        // geocaching.com settings
        final Pair<String, String> login = Settings.getLogin();
        if (login != null) {
            ((EditText) findViewById(R.id.username)).setText(login.first);
            ((EditText) findViewById(R.id.password)).setText(login.second);
        }

        Button logMeIn = (Button) findViewById(R.id.log_me_in);
        logMeIn.setOnClickListener(new logIn());

        TextView legalNote = (TextView) findViewById(R.id.legal_note);
        legalNote.setClickable(true);
        legalNote.setOnClickListener(new View.OnClickListener() {

            //@Override
            public void onClick(View arg0) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.olbg.com/use.php")));
            }
        });
		
        TextView registerNow = (TextView) findViewById(R.id.register_now);
        registerNow.setClickable(true);
        registerNow.setOnClickListener(new View.OnClickListener() {

            //@Override
            public void onClick(View arg0) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.olbg.com/?tx160039")));
            }
        });
		
		// Twitter settings
		Button authorizeTwitter = (Button) findViewById(R.id.authorize_twitter);
		authorizeTwitter.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent authIntent = new Intent(SettingsActivity.this, TwitterAuthorizationActivity.class);
				startActivity(authIntent);
			}
		});
		
		final CheckBox twitterButton = (CheckBox) findViewById(R.id.twitter_option);
		twitterButton.setChecked(Settings.isUseTwitter() && Settings.isTwitterLoginValid());
		twitterButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.setUseTwitter(twitterButton.isChecked());
				if(Settings.isUseTwitter() && !Settings.isTwitterLoginValid()) {
					Intent authIntent = new Intent(SettingsActivity.this, TwitterAuthorizationActivity.class);
					startActivity(authIntent);
				}
				twitterButton.setChecked(Settings.isUseTwitter());
			}
		});
		
		// Other settings
		final CheckBox skinButton = (CheckBox) findViewById(R.id.skin);
		skinButton.setChecked(Settings.isLightSkin());
		skinButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Settings.setLightSkin(skinButton.isChecked());
			}
		});

	}

	
	
	
	
	
	
	
	
	
	
	private class logIn implements View.OnClickListener {

        //@Override
        public void onClick(View arg0) {
            final String username = ((EditText) findViewById(R.id.username)).getText().toString();
            final String password = ((EditText) findViewById(R.id.password)).getText().toString();

            if (Utils.isBlank(username) || Utils.isBlank(password)) {
            	showToast(res.getString(R.string.err_missing_auth));
            	return;
            }

            loginDialog = ProgressDialog.show(SettingsActivity.this, res.getString(R.string.init_login_popup), res.getString(R.string.init_login_popup_working), true);
            loginDialog.setCancelable(false);

            Settings.setLogin(username, password);
            Login.cookieManager.getCookieStore().removeAll();

            (new Thread() {

                @Override
                public void run() {
                    StatusCode loginResult = StatusCode.UNKNOWN_ERROR;
					try {
						loginResult = Login.login();
					} catch (IOException e) {
					} catch (URISyntaxException e) {
					}
                    Object payload = loginResult;
                    if (loginResult == StatusCode.NO_ERROR) {
                        //Login.detectGcCustomDate();
                        //payload = Login.downloadAvatarAndGetMemberStatus();
                    	payload = null;
                    }
                    logInHandler.obtainMessage(0, payload).sendToTarget();
                }
            }).start();
        }
    }
	
	public static void startActivity(Context fromActivity) {
        final Intent initIntent = new Intent(fromActivity, SettingsActivity.class);
        fromActivity.startActivity(initIntent);
    }

}
