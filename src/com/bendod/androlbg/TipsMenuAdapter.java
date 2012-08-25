package com.bendod.androlbg;

import java.util.List;

import android.app.Activity;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TipsMenuAdapter extends ArrayAdapter<TipsMenuItem> {

	private final List<TipsMenuItem> list;
	private final Activity context;

	public TipsMenuAdapter(Activity context, int textViewResourceId,
			List<TipsMenuItem> list) {
		super(context, textViewResourceId, list);
		this.context = context;
		this.list = list;
	}

	public TipsMenuItem getItem(int index) {
        return this.list.get(index);
    }

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater=context.getLayoutInflater();
		View view=inflater.inflate(R.layout.tips_menu_item, parent, false);
		TipsMenuItem tipsMenuItem = getItem(position);
		TextView label=(TextView)view.findViewById(R.id.title);
		label.setText(tipsMenuItem.title);
		label.setCompoundDrawablesWithIntrinsicBounds(tipsMenuItem.imgSrc, null, null, null);
		
		return view;
	}
	
}
