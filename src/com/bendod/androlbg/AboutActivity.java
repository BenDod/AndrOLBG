package com.bendod.androlbg;

import com.bendod.androlbg.activity.AbstractActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.bendod.androlbg.utils.*;

public class AboutActivity extends AbstractActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.olbg_about);
		super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
		((TextView) findViewById(R.id.about_version_string)).setText(Utils.getVersionName(this));
    }

    /**
     * @param view
     *            unused here but needed since this method is referenced from XML layout
     */
    public void support(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:BenDod.Dev@gmail.com")));
    }

    /**
     * @param view
     *            unused here but needed since this method is referenced from XML layout
     */
    public void twitter(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.twitter.com/bendod_dev")));
    }

}
