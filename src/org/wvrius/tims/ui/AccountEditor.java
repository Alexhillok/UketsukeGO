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

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import org.wvrius.tims.Account;
import org.wvrius.tims.Accounts;
import org.wvrius.tims.R;
import org.wvrius.tims.net.Transport;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.Window;

public class AccountEditor extends PreferenceActivity implements OnPreferenceChangeListener{

	public static final String PREF_DISPLAY_NAME = "displayName";
	public static final String PREF_ACCOUNT_NAME = "accountName";
	public static final String PREF_PASSWORD = "editPassword";
	public static final String PREF_USER_NAME = "userName";
	public static final String PREF_DOMAIN = "domainName";
	public static final String PREF_PROXY = "proxyAddress";
	public static final String PREF_AUTH_NAME = "authorizationName";
	public static final String PREF_PROXY_PORT = "proxyPort";
	public static final String PREF_TRANSPORT = "transportProtocol";
	public static final String PREF_REGISTER_ON_STARTUP = "registerOnStartup";  
		
	EditTextPreference displayNamePref;
	EditTextPreference accountNamePref;
	EditTextPreference passwordPref;
	EditTextPreference userNamePref;
	EditTextPreference domainPref;
	EditTextPreference proxyPref;
	EditTextPreference authNamePref;
	EditTextPreference proxyPortPref;
	ListPreference transportPref;
	CheckBoxPreference registerOnStartupPref;
	
	
	private Account account;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.account_pref);
		
		String accountUID = (String)this.getIntent().getExtras().get("accountUID");
		account = Accounts.getAccount(accountUID);
		if(account == null)
		{
			throw new RuntimeException("Account should not be null");
		}
		
		loadFields();
	}
	
	public void loadFields()
	{
		PreferenceScreen screen = this.getPreferenceScreen();
		
		/* Get preference fields */
		accountNamePref = (EditTextPreference) getPreferenceManager().findPreference(PREF_ACCOUNT_NAME);
		displayNamePref = (EditTextPreference) getPreferenceManager().findPreference(PREF_DISPLAY_NAME);
		passwordPref = (EditTextPreference) getPreferenceManager().findPreference(PREF_PASSWORD);
		userNamePref = (EditTextPreference) getPreferenceManager().findPreference(PREF_USER_NAME);
		domainPref = (EditTextPreference) getPreferenceManager().findPreference(PREF_DOMAIN);
		proxyPref = (EditTextPreference) getPreferenceManager().findPreference(PREF_PROXY);
		authNamePref = (EditTextPreference) getPreferenceManager().findPreference(PREF_AUTH_NAME);
		proxyPortPref = (EditTextPreference) getPreferenceManager().findPreference(PREF_PROXY_PORT);
		transportPref = (ListPreference) getPreferenceManager().findPreference(PREF_TRANSPORT);
		registerOnStartupPref = (CheckBoxPreference) getPreferenceManager().findPreference(PREF_REGISTER_ON_STARTUP);
		
		/*
		for(String key : this.getPreferenceManager().getSharedPreferences().getAll().keySet())
		{
			Preference p = getPreferenceManager().findPreference(key);
			if(p != null)
			{
				p.setOnPreferenceChangeListener(this);
			}
		}
		*/
		
		/* Set values */
		accountNamePref.setText(account.getAccountName());
		displayNamePref.setText(account.getDisplayName());
		passwordPref.setText(account.getPassword());
		userNamePref.setText(account.getUserName());
		domainPref.setText(account.getDomainName());
		proxyPref.setText(account.getOutboundServer());
		authNamePref.setText(account.getAuthorizationName());
		proxyPortPref.setText(String.valueOf(account.getProxyPort()));
		registerOnStartupPref.setChecked(account.RegisterOnStart());
		transportPref.setValue(Transport.protocolToString(account.getPreferedTransport()));
	}
	
	public void updateAccountValues()
	{
		account.setAccountName(accountNamePref.getText());
		account.setDisplayName(displayNamePref.getText());
		account.setAuthorizationName(authNamePref.getText());
		account.setDomainName(domainPref.getText());
		account.setOutboundServer(proxyPref.getText());
		//TODO : Password
		account.setPassword(passwordPref.getText());
		account.setUserName(userNamePref.getText());
		
		account.setProxyPort(proxyPortPref.getText());
		account.setRegisterOnStartup(registerOnStartupPref.isChecked());
		account.setPreferedTransport(Transport.parseProtocol(transportPref.getValue()));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
	
	@Override
	public void onBackPressed() {
		
		updateAccountValues();
		String[] errors = account.validateSettings();
		if(errors.length > 0)
		{
			showDialog(errors[0]);
		}
		else
		{
			Accounts.saveAccount(account);
			super.onBackPressed();
		}
	}
	
	private void showDialog(String errorMessage)
	{
		
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();  
	    alertDialog.setTitle("Error");  
	    alertDialog.setMessage(errorMessage + "");  
        alertDialog.setButton("Continue", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Accounts.dismissTemporaryAccount(account);
				onPause();
				return;
			}
		});   
		    alertDialog.setButton2("Fix error", new DialogInterface.OnClickListener() {  
		      public void onClick(DialogInterface dialog, int which) {  
  
		    	return;  
		    }});
		    alertDialog.show();
	}
	

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference instanceof EditTextPreference)
		{
			EditTextPreference edp = (EditTextPreference)preference;
			String value = (String) newValue;
			if(value != "")
				edp.setSummary(value);
			else
				edp.setSummary("<empty>");
		} else if(preference instanceof ListPreference)
		{
			
		}
		
		return true;
	}

	
	
}
