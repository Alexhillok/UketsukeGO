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
package org.wvrius.tims.net;

import java.util.HashMap;
import java.util.HashSet;
import java.util.TooManyListenersException;

import javax.sip.ClientTransaction;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.wvrius.tims.Account;
import org.wvrius.tims.IDisposable;
import org.wvrius.tims.TIMSEngine;

import android.util.Log;


public class MessageProcessor implements SipListener, IDisposable {
	
	Account account;
	SipProvider sipProvider;
	AddressFactory addressFactory;
	MessageFactory messageFactory;
	HeaderFactory headerFactory;
	
	HashMap<String, HashSet<SipListener>> messageSipListeners;
	
	
	public MessageProcessor(Account _account)
	{
		account = _account;
		sipProvider = TIMSEngine.getAccountSipProvider(_account);
		addressFactory = TIMSEngine.getAddressFactory();
		messageFactory = TIMSEngine.getMessageFactory();
		headerFactory = TIMSEngine.getHeaderFactory();
		messageSipListeners = new HashMap<String, HashSet<SipListener>>();	
		
		try {
			sipProvider.addSipListener(this);
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void registerMessageSipListener(String method, SipListener listener)
	{
		HashSet<SipListener> listeners = messageSipListeners.get(method);
		if(listeners == null)
			listeners = new HashSet<SipListener>();
		
		listeners.add(listener);
		messageSipListeners.put(method, listeners);
	}
	
	public void unregisterMessageSipListener(String method, SipListener listener)
	{
		HashSet<SipListener> listeners = messageSipListeners.get(method);
		if(listeners != null)
		{
			listeners.remove(listener);
			messageSipListeners.put(method, listeners);
		}
	}
	
	public void unregisterMessageSipListener(SipListener listener)
	{
		for(String method : messageSipListeners.keySet())
		{
			HashSet<SipListener> listeners = messageSipListeners.get(method);
			if(listeners != null)
			{
				listeners.remove(listener);
				messageSipListeners.put(method, listeners);
			}
		}	
	}


	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processRequest(RequestEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processResponse(ResponseEvent responseEvent) {
		
		Log.d("SIP", "Received response - " + responseEvent.getResponse().getStatusCode());
		String method;
		ClientTransaction transaction;
		
		transaction = responseEvent.getClientTransaction();
		method = transaction.getRequest().getMethod();
		
		HashSet<SipListener> listeners = messageSipListeners.get(method);
		if(listeners != null)
		{
			for(SipListener l : listeners)
			{
				l.processResponse(responseEvent);
			}
		}
	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void sendWithTransaction(Request request)
	{
		ClientTransaction ct;

		try {
			ct = sipProvider.getNewClientTransaction(request);
			ct.sendRequest();
		} catch (TransactionUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void dispose() {
		sipProvider.removeSipListener(this);		
	}

}
