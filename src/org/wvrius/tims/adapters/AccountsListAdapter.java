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

import java.util.ArrayList;
import java.util.HashMap;
import org.wvrius.tims.Account;
import org.wvrius.tims.Accounts;
import org.wvrius.tims.IOnAccountChangeListener;
import org.wvrius.tims.IOnAccountsChangeListener;
import org.wvrius.tims.IOnRegisterStatusChangeListener;
import org.wvrius.tims.OnCheckedChangeListener;
import org.wvrius.tims.R;
import org.wvrius.tims.TIMSEngine;
import org.wvrius.tims.net.RegisterAgent;
import org.wvrius.tims.net.Agent.AgentType;
import org.wvrius.tims.sip.RegisterState.State;
import org.wvrius.tims.ui.AccountsList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


public class AccountsListAdapter extends BaseAdapter implements IOnAccountsChangeListener, IOnAccountChangeListener, IOnRegisterStatusChangeListener {

	Context context;
	HashMap<String,View> accountsMap;
	HashMap<View, Account> viewMap;
    ArrayList<Account> accounts;
    
    final Handler registrationChangedHandler;
    
	public AccountsListAdapter(Context c) {
		
		
		super();
	
		registrationChangedHandler = new Handler() {
		        public void handleMessage(Message msg) {
		        	Bundle data = msg.getData();
		        	State newStatus = (State)msg.obj;
		        	String accountID = data.getString("accountID");
		            Account account = Accounts.getAccount(accountID);
		            View listItem = accountsMap.get(account.getUID());
		    	    if(listItem != null)
		    	    {
		    	    	updateState(listItem, newStatus);
		    	    }	
		    	    notifyDataSetChanged();
		        }
		    };
		
		Accounts.registerOnChangeListener(this);
		accountsMap = new HashMap<String, View>();
		viewMap = new HashMap<View, Account>();
		accounts = new ArrayList<Account>();
			
		
		this.context = c;
		
		fillAccounts();
		
	}
	
	private void fillAccounts()
	{
		accountsMap.clear();
		accounts.clear();
		viewMap.clear();
		
		for(Account ac : Accounts.getDatabaseAccounts())
		{
			accounts.add(ac);
	        ac.registerOnAccountChangeListener(this);
	        RegisterAgent ra = (RegisterAgent) TIMSEngine.GetAgent(ac, AgentType.REGISTER);
	        ra.registerChangeListener(this);
		}
	}
	
	public void fillItem(View v, Account ac)
	{
		TextView tv = (TextView) v.findViewById(R.id.account_name);
		tv.setText(ac.getAccountName());
		
		RegisterAgent rag = (RegisterAgent) TIMSEngine.GetAgent(ac, RegisterAgent.MyType());
		if(rag != null)
		{
			TextView tvs = (TextView) v.findViewById(R.id.account_status);
			tvs.setText(rag.getState().toString());
		}
	}
	
	public void updateState(View v, State newSTate)
	{
		TextView tv = (TextView) v.findViewById(R.id.account_status);
		tv.setText(newSTate.toString());
	}
	
	public View getView(int pos, View inView, ViewGroup parent) {
	       
		Account item = (Account)this.getItem(pos);
	       
		
			View v = accountsMap.get(item.getUID()); 
	       if (v == null) {
	            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            v = inflater.inflate(R.layout.accounts_element,null);  
	       
	            CheckBox enableRegister = (CheckBox) v.findViewById(R.id.enable_register);
	            enableRegister.setFocusable(false);
	            if(item.RegisterOnStart())
	            	enableRegister.setChecked(true);
	            
	            enableRegister.setOnCheckedChangeListener(new OnCheckedChangeListener(item));
	            
	            accountsMap.put(item.getUID(), v);
	            viewMap.put(v,item);
	            
	       }	
	       
	       fillItem(v, item);
	       
	       
	       
	       return(v);
	}

	@Override
	public int getCount() {
		return accounts.size();
	}

	public Account getAccountByView(View v)
	{
		return viewMap.get(v);
	}



	@Override
	public Object getItem(int arg0) {
		return accounts.get(arg0);
	}




	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void OnAccountsChange() {
		
		fillAccounts();
	
		notifyDataSetChanged();
	}

	@Override
	public void OnAccountChange(Account account) {

		View listItem = accountsMap.get(account.getUID());
	    if(listItem != null)
	    {
	    	fillItem(listItem, account);
	    }	
	    notifyDataSetChanged();
	}

	@Override
	public void registerStateChanged(Account account, State newStatus) {
		Message msg = new Message();
		Bundle data = new Bundle();
		data.putString("accountID", account.getUID());
		msg.setData(data);
		msg.obj = newStatus;
		registrationChangedHandler.sendMessage(msg);
	}
	

}
