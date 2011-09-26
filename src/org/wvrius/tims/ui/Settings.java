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
package org.wvrius.tims.ui;

import org.wvrius.tims.R;
import org.wvrius.tims.adapters.SettingsListAdapter;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Settings extends ListActivity implements OnItemClickListener {
	 
	  
	  	String[] items;
	  	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	       items = getResources().getStringArray(R.array.settings_list);
	        setContentView(R.layout.settings);
	      
	        setListAdapter(new SettingsListAdapter(this, R.layout.settings_element, items));
	        ListView lv = (ListView) this.findViewById(android.R.id.list);
	        lv.setOnItemClickListener(this);
	    }
	  
	  public void showAccountsList(View view)
	  {
		  MessageBox.ShowMessage(this, "Show Accounts List");
	  }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		
		String item = items[position];
		String[] split = item.split(";");
		int item_id = Integer.parseInt(split[3]);
		switch(item_id)
		{
		case 1:
		{
			showAccounts();
		}
		break;
		}
		
	}

	public void showAccounts()
	{
		Intent intent = new Intent(Settings.this, org.wvrius.tims.ui.AccountsList.class);
        startActivity(intent);
	}
	  

	
	    
}
