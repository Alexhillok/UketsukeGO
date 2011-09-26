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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.wvrius.tims.db.DatabaseHelper;
import org.wvrius.tims.net.Transport;

public class Account {
	
	private long _id;
	private String uid;
	private String accountName;
	private String displayName;
	private String password;
	private String authorizationName;
	private String domainName;
	private String userName;
	private String server;
	private Boolean registerOnStartup;
	private int preferedTransport;
	private int proxyPort;
	
	private HashSet<IOnAccountChangeListener> accountChangeListeners;
	
	public void registerOnAccountChangeListener(IOnAccountChangeListener listener)
	{
		accountChangeListeners.add(listener);
	}
	
	public void unregisterOnAccountChangeListener(IOnAccountChangeListener listener)
	{
		accountChangeListeners.remove(listener);
	}
	
	private void init()
	{
		accountChangeListeners = new HashSet<IOnAccountChangeListener>();
	}
	
	public Account()
	{
		UUID randomUUID = UUID.randomUUID();
		this.setUID(randomUUID.toString());
		this.setAccountName("");
		this.setDisplayName("");
		this.setPassword("");
		this.setAuthorizationName("");
		this.setDomainName("");
		this.setUserName("");
		this.setOutboundServer("");
		this.setRegisterOnStartup(false);
		this.setID(-1);
		this.setPreferedTransport(Transport.PROTOCOL_UDP);
		setProxyPort(5060);
		init();
	}	
	
	public Account(int _id,String uid, String accountName, String displayName, String username, String password, String domainName, String outboundProxy, String authorizationName, int registerOnStartup, int proxyPort, int transport)
	{
		this.setID(_id);
		this.setUID(uid);
		this.setAccountName(accountName);
		this.setDisplayName(displayName);
		this.setPassword(password);
		this.setAuthorizationName(authorizationName);
		this.setDomainName(domainName);
		this.setUserName(username);
		this.setOutboundServer(outboundProxy);
		this.setRegisterOnStartup(registerOnStartup == 1);
		this.setProxyPort(proxyPort);
		this.setPreferedTransport(transport);
		init();
	}
	
	public String[] validateSettings()
	{
		ArrayList<String> result = new ArrayList<String>();
		if(this.accountName.trim().length() == 0)
		{
			result.add("Account name cannot be empty");
		}
		
		if(this.displayName.trim().length() == 0)
		{
			result.add("Display name cannot be empty");
		}
		
		if(this.server.trim().length() == 0)
		{
			result.add("Server/Proxy cannot be empty");
		}
		
		if(this.userName.trim().length() == 0)
		{
			result.add("Username cannot be empty");
		}
		
		if(this.proxyPort <= 0 || this.proxyPort > 65540)
		{
			result.add("Invalid server/proxy port");
		}
		
		String sresult[] = new String[result.size()];
		sresult = result.toArray(sresult);
		
		return sresult;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the authorizationName
	 */
	public String getAuthorizationName() {
		return authorizationName;
	}

	/**
	 * @param authorizationName the authorizationName to set
	 */
	public void setAuthorizationName(String authorizationName) {
		this.authorizationName = authorizationName;
	}

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}


	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}


	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	/**
	 * @return the accountName
	 */
	public String getAccountName() {
		return accountName;
	}


	/**
	 * @param accountName the accountName to set
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}


	/**
	 * @return the UID
	 */
	public String getUID() {
		return uid;
	}


	/**
	 * @param UID the UID to set
	 */
	public void setUID(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the outboundProxy
	 */
	public String getOutboundServer() {
		return server;
	}

	/**
	 * @param outboundProxy the outboundProxy to set
	 */
	public void setOutboundServer(String outboundProxy) {
		this.server = outboundProxy;
	}

	public void notifyChange()
	{
		for(IOnAccountChangeListener listener : accountChangeListeners)
		{
			listener.OnAccountChange(this);
		}
	}

	/**
	 * @return the _id
	 */
	public long getID() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void setID(long _id) {
		this._id = _id;
	}

	/**
	 * @return the autoLogin
	 */
	public Boolean RegisterOnStart() {
		return registerOnStartup;
	}

	/**
	 * @param autoLogin the autoLogin to set
	 */
	public void setRegisterOnStartup(Boolean autoLogin) {
		this.registerOnStartup = autoLogin;
	}
	
	public Boolean saveToDatabase()
	{
		DatabaseHelper db = new DatabaseHelper(TIMSEngine.getMainContext());
		
		if(this._id == -1)
		{
			return db.insertAccount(this);
		}
		else
		{
			Boolean result = db.updateAccount(this);
			if(result)
				notifyChange();
			
			return result;
		}
	}

	public int getPreferedTransport() {
		return preferedTransport;
	}

	public void setPreferedTransport(int preferedTransport) {
		this.preferedTransport = preferedTransport;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}	


	public void setProxyPort(String proxyPort) {
		try
		{
			this.proxyPort = Integer.parseInt(proxyPort);
		}
		catch(Exception ex)
		{
			this.proxyPort = -1;
		}
	}	
	
	@Override
	public int hashCode() {
		return this.uid.hashCode();
	}
	
}
