/*******************************************************************************
 * This file is part of TIMS (http://github.com/wvrius/android-tims/)
 * 
 * Copyright (c) 2011 Marius Ungureanu <marius@wvrius.com> - All rights reserved.
 * 
 * TIMS is free software; you can redistribute it and/or modify  it under the terms 
 * of the GNU General Public License V3 as published by the Free Software Foundation
 *  
 * This software is distribuited WITHOUT ANY WARRANTY.
 ******************************************************************************/
package org.wvrius.tims.adapters;

import java.util.List;

import org.wvrius.tims.R;
import org.wvrius.tims.R.drawable;



import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsListAdapter extends ArrayAdapter<String> {

	
	public SettingsListAdapter(Context context, int textViewResourceId,
			String[] objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		
	}



	Context context;
	

	
	public View getView(int pos, View inView, ViewGroup parent) {
	       View v = inView;
	       if (v == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflater.inflate(R.layout.settings_element, null);
	            
	           
	       }
	       String item = this.getItem(pos);
	       String[] split = item.split(";");
	       
	       TextView tview = (TextView) v.findViewById(R.id.settings_item_text);
	       tview.setText(split[0]);
	       
	       ImageView image = (ImageView) v.findViewById(R.id.settings_item_image);

	       int resId = context.getResources().getIdentifier(split[2], "drawable", context.getPackageName()); 
	       
	       Bitmap _image = BitmapFactory.decodeResource(context.getResources(), resId); 
	       image.setImageBitmap(_image);
	       
	       TextView dview = (TextView) v.findViewById(R.id.settings_item_desc);
	       dview.setText(split[1]);
	       
	       
	       return(v);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
