package com.bendod.androlbg;

import com.bendod.androlbg.activity.AbstractListActivity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class TipsMenuActivity extends AbstractListActivity {
	
	public final static String TIPS_MENU_ITEM = "TIPS_MENU_ITEM";
	public final static String TIPS_MENU_TITLE = "TIPS_MENU_TITLE";
	private List<TipsMenuItem> tipsMenuList= new ArrayList<TipsMenuItem>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		int tipId = intent.getIntExtra(TipsMenuActivity.TIPS_MENU_ITEM,0);
		int tipTitle = intent.getIntExtra(TipsMenuActivity.TIPS_MENU_TITLE,0);
		// Set the View layer
		setContentView(R.layout.tips_menu);
		if(tipTitle==0){
			actionBar.setTitle(R.string.make_tip_button);
		}else{
			actionBar.setTitle(tipTitle);
		}
		
		switch (tipId) {
		case 1:
			loadTipsMenuFromResource(R.xml.football_headers, tipsMenuList);
			break;
		case 5:
			loadTipsMenuFromResource(R.xml.us_sports_headers, tipsMenuList);
			break;
		case 12:
			loadTipsMenuFromResource(R.xml.rugby_headers, tipsMenuList);
			break;
		case 14:
			loadTipsMenuFromResource(R.xml.english_football_headers, tipsMenuList);
			break;
		case 15:
			loadTipsMenuFromResource(R.xml.european_comps_headers, tipsMenuList);
			break;
		case 21:
			loadTipsMenuFromResource(R.xml.european_leagues_headers, tipsMenuList);
			break;
		case 22:
			loadTipsMenuFromResource(R.xml.international_headers, tipsMenuList);
			break;
		case 26:
			loadTipsMenuFromResource(R.xml.world_leagues_headers, tipsMenuList);
			break;
		case 30:
			loadTipsMenuFromResource(R.xml.scottish_football_headers, tipsMenuList);
			break;
		default:
			loadTipsMenuFromResource(R.xml.tips_headers, tipsMenuList);
			break;
		}
		
		
		TipsMenuAdapter adapter = new TipsMenuAdapter(this,
				R.layout.tips_menu_item, tipsMenuList);
		setListAdapter(adapter);
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TipsMenuItem item = (TipsMenuItem) getListAdapter().getItem(position);
		Intent intent = new Intent();
		if("MakeTipActivity".equals(item.intent)){
			intent.setClass(this, MakeTipActivity.class);
		}else{
			intent.setClass(this, TipsMenuActivity.class);
		}
		intent.putExtra(TIPS_MENU_ITEM, item.tipId);
		intent.putExtra(TIPS_MENU_TITLE, item.title);
		startActivity(intent);
	}
	
	private void loadTipsMenuFromResource(int resid, List<TipsMenuItem> target) {
		XmlResourceParser parser = null;
		try {
			parser = getResources().getXml(resid);
			
			int type;
            while ((type=parser.next()) != XmlResourceParser.END_DOCUMENT
                    && type != XmlResourceParser.START_TAG) {
                // Parse next until start tag is found
            }

            String nodeName = parser.getName();
            if (!"tips-headers".equals(nodeName)) {
                throw new RuntimeException(
                        "XML document must start with <tips-headers> tag; found"
                        + nodeName + " at " + parser.getPositionDescription());
            }

            final int outerDepth = parser.getDepth();
            while ((type=parser.next()) != XmlResourceParser.END_DOCUMENT
                   && (type != XmlResourceParser.END_TAG || parser.getDepth() > outerDepth)) {
                if (type == XmlResourceParser.END_TAG || type == XmlResourceParser.TEXT) {
                    continue;
                }

                nodeName = parser.getName();
                if ("header".equals(nodeName)) {
                	TipsMenuItem tipsMenuItem = new TipsMenuItem();
                	tipsMenuItem.tipId = Integer.parseInt(parser.getAttributeValue(null, "tipId"));
                	try {
                	    Class res = R.string.class;
                	    Field field = res.getField(parser.getAttributeValue(null, "title"));
                	    tipsMenuItem.title = field.getInt(null);
                	}
                	catch (Exception e) {
                	    Log.e("MyTag", "Failure to get drawable id.", e);
                	}
                	try {
                	    Class res = R.drawable.class;
                	    Field field = res.getField(parser.getAttributeValue(null, "icon"));
                	    tipsMenuItem.imgSrc = getResources().getDrawable(field.getInt(null));
                	}
                	catch (Exception e) {
                	    Log.e("MyTag", "Failure to get drawable id.", e);
                	}
                	tipsMenuItem.intent = parser.getAttributeValue(null, "intent");
                	
                    target.add(tipsMenuItem);
                } else {
                  //  XmlUtils.skipCurrentTag(parser);
                } 
            } 
		} catch (XmlPullParserException e) {
			throw new RuntimeException("Error parsing headers", e);
		} catch (IOException e) {
			throw new RuntimeException("Error parsing headers", e);
		} finally {
			if (parser != null) parser.close();
		}
	}
}
