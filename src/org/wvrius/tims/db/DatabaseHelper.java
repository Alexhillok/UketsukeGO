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
package org.wvrius.tims.db;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.TTCCLayout;
import org.wvrius.tims.Account;
import org.wvrius.tims.net.Transport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper  {
	

	private static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "tims_database";
    public static final String TABLE_ACCOUNTS = "accounts";
    
    public static final String TABLE_ACCOUNTS_FIELD_ID = "_id";
    public static final String TABLE_ACCOUNTS_FIELD_UID = "uid";
    public static final String ACCOUNTS_TABLE_ACCOUNTNAME_FIELD = "accountname";
    public static final String ACCOUNTS_TABLE_USERNAME_FIELD = "username";
    public static final String ACCOUNTS_TABLE_DISPLAYNAME_FIELD = "displayName";
    public static final String ACCOUNTS_TABLE_PASSWORD_FIELD = "password";
    public static final String ACCOUNTS_TABLE_DOMAIN_FIELD = "domain";
    public static final String ACCOUNTS_TABLE_PROXY_FIELD = "outboundProxy";
    public static final String ACCOUNTS_TABLE_AUTHNAME_FIELD = "authorizationName";
    public static final String TABLE_ACCOUNTS_FIELD_AUTOREGISTER = "autoregister";
    public static final String TABLE_ACCOUNTS_FIELD_PORT = "port";
    public static final String TABLE_ACCOUNTS_FIELD_TRANSPORT = "transport";
    
    public static final String[] TABLE_ACCOUNTS_FIELDS = new String[]{TABLE_ACCOUNTS_FIELD_ID, TABLE_ACCOUNTS_FIELD_UID, ACCOUNTS_TABLE_ACCOUNTNAME_FIELD,
    	ACCOUNTS_TABLE_DISPLAYNAME_FIELD, ACCOUNTS_TABLE_USERNAME_FIELD, ACCOUNTS_TABLE_PASSWORD_FIELD,  ACCOUNTS_TABLE_DOMAIN_FIELD,
    	ACCOUNTS_TABLE_PROXY_FIELD, ACCOUNTS_TABLE_AUTHNAME_FIELD, TABLE_ACCOUNTS_FIELD_AUTOREGISTER, TABLE_ACCOUNTS_FIELD_PORT, TABLE_ACCOUNTS_FIELD_TRANSPORT};
    
    public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, 1);
    }
    
    @Override
    public void onOpen(SQLiteDatabase db) {
    	super.onOpen(db);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		String table_accounts = "CREATE TABLE IF NOT EXISTS "+TABLE_ACCOUNTS+" (" +
                TABLE_ACCOUNTS_FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				TABLE_ACCOUNTS_FIELD_UID +" TEXT UNIQUE," +
				ACCOUNTS_TABLE_ACCOUNTNAME_FIELD + " TEXT, " +
				ACCOUNTS_TABLE_USERNAME_FIELD + " TEXT, " +
                ACCOUNTS_TABLE_DISPLAYNAME_FIELD + " TEXT, " +
                ACCOUNTS_TABLE_PASSWORD_FIELD + " TEXT, " +
                ACCOUNTS_TABLE_DOMAIN_FIELD + " TEXT, " +
                ACCOUNTS_TABLE_PROXY_FIELD + " TEXT, " +
                ACCOUNTS_TABLE_AUTHNAME_FIELD + " TEXT, " +
                TABLE_ACCOUNTS_FIELD_PORT + " INTEGER, " +
                TABLE_ACCOUNTS_FIELD_TRANSPORT + " INTEGER," + 
                TABLE_ACCOUNTS_FIELD_AUTOREGISTER + " INTEGER )";
		
		
		db.execSQL(table_accounts);
		fillDatabaseWithJunk(db);
	}
	
	public void fillDatabaseWithJunk(SQLiteDatabase db)
	{
		/*
        ContentValues values = new ContentValues();

        values.put(TABLE_ACCOUNTS_FIELD_UID, "2123-435g-3hfjf-453sg");
        values.put(ACCOUNTS_TABLE_ACCOUNTNAME_FIELD, "demo@testnet.ro");
        values.put(ACCOUNTS_TABLE_USERNAME_FIELD, "demo");
        values.put(ACCOUNTS_TABLE_DISPLAYNAME_FIELD, "Demo");
        values.put(ACCOUNTS_TABLE_PASSWORD_FIELD, "demo");
        values.put(ACCOUNTS_TABLE_DOMAIN_FIELD, "");
        values.put(ACCOUNTS_TABLE_PROXY_FIELD, "testnet.ro");
        values.put(ACCOUNTS_TABLE_AUTHNAME_FIELD, "demo@testnet.ro");
        values.put(TABLE_ACCOUNTS_FIELD_AUTOREGISTER, 1);
        values.put(TABLE_ACCOUNTS_FIELD_PORT, 4060);
        values.put(TABLE_ACCOUNTS_FIELD_TRANSPORT, Transport.PROTOCOL_UDP);
       
        
        db.insert ("accounts", "accountName", values);
         */
	}
	
	public Boolean insertAccount(Account account)
	{
			SQLiteDatabase db = this.getWritableDatabase();
		 ContentValues values = new ContentValues();

		 
	        values.put(TABLE_ACCOUNTS_FIELD_UID, account.getUID());
	        values.put(ACCOUNTS_TABLE_ACCOUNTNAME_FIELD, account.getAccountName());
	        values.put(ACCOUNTS_TABLE_USERNAME_FIELD, account.getUserName());
	        values.put(ACCOUNTS_TABLE_DISPLAYNAME_FIELD, account.getDisplayName());
	        values.put(ACCOUNTS_TABLE_PASSWORD_FIELD, account.getPassword());
	        values.put(ACCOUNTS_TABLE_DOMAIN_FIELD, account.getDomainName());
	        values.put(ACCOUNTS_TABLE_PROXY_FIELD, account.getOutboundServer());
	        values.put(ACCOUNTS_TABLE_AUTHNAME_FIELD, account.getAuthorizationName());
	        values.put(TABLE_ACCOUNTS_FIELD_AUTOREGISTER, account.RegisterOnStart() ? 1 : 0);
	        values.put(TABLE_ACCOUNTS_FIELD_PORT, account.getProxyPort());
	        values.put(TABLE_ACCOUNTS_FIELD_TRANSPORT, account.getPreferedTransport());
	        
	        long id = db.insert (TABLE_ACCOUNTS, ACCOUNTS_TABLE_ACCOUNTNAME_FIELD, values);
	        
	        if(id != -1)
	        {
	        	account.setID(id);
	        	return true;
	        }
	        
	        return false;
	}

	public Boolean updateAccount(Account account)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
	    values.put(ACCOUNTS_TABLE_ACCOUNTNAME_FIELD, account.getAccountName());
        values.put(ACCOUNTS_TABLE_USERNAME_FIELD, account.getUserName());
        values.put(ACCOUNTS_TABLE_DISPLAYNAME_FIELD, account.getDisplayName());
        values.put(ACCOUNTS_TABLE_PASSWORD_FIELD, account.getPassword());
        values.put(ACCOUNTS_TABLE_DOMAIN_FIELD, account.getDomainName());
        values.put(ACCOUNTS_TABLE_PROXY_FIELD, account.getOutboundServer());
        values.put(ACCOUNTS_TABLE_AUTHNAME_FIELD, account.getAuthorizationName());
        values.put(TABLE_ACCOUNTS_FIELD_AUTOREGISTER, account.RegisterOnStart() ? 1 : 0);
        values.put(TABLE_ACCOUNTS_FIELD_PORT, account.getProxyPort());
        values.put(TABLE_ACCOUNTS_FIELD_TRANSPORT, account.getPreferedTransport());
        
	    
        int rows = db.update(TABLE_ACCOUNTS, values, TABLE_ACCOUNTS_FIELD_ID+"=?", new String[] {Long.toString(account.getID())});
		db.close();
        if(rows != 0)
	       return true;
		
		return false;
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        onCreate(db);
	}
	
	public List<Account> getAccounts()
	{
		ArrayList<Account> result = new ArrayList<Account>();
		SQLiteDatabase db=this.getReadableDatabase();
		Cursor c =  db.query (TABLE_ACCOUNTS,TABLE_ACCOUNTS_FIELDS,null,null,null, null, null);
		c.move(-1);
		while(c.moveToNext())
		{
			int _id = c.getInt(c.getColumnIndexOrThrow(TABLE_ACCOUNTS_FIELD_ID));
			String uid = c.getString(c.getColumnIndexOrThrow(TABLE_ACCOUNTS_FIELD_UID));
			String accountName = c.getString(c.getColumnIndexOrThrow(ACCOUNTS_TABLE_ACCOUNTNAME_FIELD));
			String displayName = c.getString(c.getColumnIndex(ACCOUNTS_TABLE_DISPLAYNAME_FIELD));
			String username = c.getString(c.getColumnIndexOrThrow(ACCOUNTS_TABLE_USERNAME_FIELD));			
			String password = c.getString(c.getColumnIndexOrThrow(ACCOUNTS_TABLE_PASSWORD_FIELD));
			String domain = c.getString(c.getColumnIndexOrThrow(ACCOUNTS_TABLE_DOMAIN_FIELD));
			String outboundProxy = c.getString(c.getColumnIndexOrThrow(ACCOUNTS_TABLE_PROXY_FIELD));
			String authorizationName = c.getString(c.getColumnIndex(ACCOUNTS_TABLE_AUTHNAME_FIELD));
			Integer autologin = c.getInt(c.getColumnIndex(TABLE_ACCOUNTS_FIELD_AUTOREGISTER));
			Integer proxyPort = c.getInt(c.getColumnIndex(TABLE_ACCOUNTS_FIELD_PORT));
			Integer transport = c.getInt(c.getColumnIndex(TABLE_ACCOUNTS_FIELD_TRANSPORT));
			
			
			Account ac = new Account(_id, uid, accountName, displayName, username, password, domain, outboundProxy, authorizationName, autologin, proxyPort, transport);
			result.add(ac);
		}
		c.close();
		
		return result;
	}

}
