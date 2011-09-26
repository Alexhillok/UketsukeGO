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
package org.wvrius.tims;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.wvrius.tims.db.DatabaseHelper;
import org.wvrius.tims.ui.TimsActivity;

import android.accounts.OnAccountsUpdateListener;
import android.content.Context;

public class Accounts {

	private static HashMap<String, Account> tempAccounts;
	private static HashMap<String, Account> dbAccounts;
	private static DatabaseHelper dbHelper;
	
	private static HashSet<IOnAccountsChangeListener> accountsChangeListeners;
	
	public static void registerOnChangeListener(IOnAccountsChangeListener listener)
	{
		accountsChangeListeners.add(listener);
	}
	
	public static void unregisterOnChangeListener(IOnAccountsChangeListener listener)
	{
		accountsChangeListeners.remove(listener);
	}

	public static void initialize()
	{
			
		dbHelper = new DatabaseHelper(TIMSEngine.getMainContext());
		dbAccounts = new HashMap<String, Account>();
		tempAccounts = new HashMap<String, Account>();
		accountsChangeListeners = new HashSet<IOnAccountsChangeListener>();
		loadAccounts();
	}
	
	public static void dismissTemporaryAccount(Account ac)
	{
		tempAccounts.remove(ac.getUID());
	}
		
	private static void loadAccounts()
	{
		List<Account> accounts = dbHelper.getAccounts();
		for(Account ac : accounts)
		{
			dbAccounts.put(ac.getUID(), ac);
		}
	}
	
	public static Account getAccount(String accountID)
	{
		Account result =  dbAccounts.get(accountID);
		if(result == null)
		{
			result = tempAccounts.get(accountID);
		}
		
		return result;
	}
	
	
	public static Account createAccount()
	{
		Account acc = new Account();
		tempAccounts.put(acc.getUID(), acc);
		return acc;
	}
	
	public static Collection<Account> getDatabaseAccounts()
	{
		return dbAccounts.values();
	}
	
	public static void saveAccount(Account account)
	{
		if(account.validateSettings().length > 0)
		{
			throw new RuntimeException("Account should be saved in a valid state");
		}
		
		account.saveToDatabase();
		
		if(tempAccounts.keySet().contains(account.getUID()))
		{
			tempAccounts.remove(account.getUID());
			addAccount(account);
		}
	}
	
	public static void addAccount(Account account)
	{
		dbAccounts.put(account.getUID(), account);
		notifyChange();
	}
	
	public static void notifyChange()
	{
		for(IOnAccountsChangeListener listener : accountsChangeListeners)
		{
			listener.OnAccountsChange();
		}
	}
	
}
