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


import org.wvrius.tims.Account;
import org.wvrius.tims.Accounts;
import org.wvrius.tims.IOnAccountsChangeListener;
import org.wvrius.tims.R;
import org.wvrius.tims.TIMSEngine;
import org.wvrius.tims.adapters.AccountsListAdapter;
import org.wvrius.tims.net.RegisterAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class AccountsList extends Activity implements OnItemClickListener {
	
	ListView lv;
	AccountsListAdapter adapter;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.accounts_list);
		adapter = new AccountsListAdapter(this);
		lv = (ListView) this.findViewById(R.id.accounts_listview);
		lv.setAdapter(adapter);
		lv.setClickable(true);
		lv.setOnItemClickListener(this);
	}

	
	public void createAccount(View view)
    {
		 Intent myIntent = new Intent(this, AccountEditor.class);
		 Account acc = Accounts.createAccount();
		 myIntent.putExtra("accountUID", acc.getUID());
		 startActivityForResult(myIntent, 0);
    }
	 
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


@Override
public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	Account ac = (Account)adapter.getItem(position);
	Intent myIntent = new Intent(this, AccountEditor.class);
	myIntent.putExtra("accountUID", ac.getUID());
	startActivityForResult(myIntent, 0);
}





	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
}
