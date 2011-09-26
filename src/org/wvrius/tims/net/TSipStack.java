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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;

import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.PeerUnavailableException;
import javax.sip.SipException;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.TransportAlreadySupportedException;
import javax.sip.TransportNotSupportedException;

import org.wvrius.tims.Account;
import org.wvrius.tims.IOnAccountChangeListener;
import org.wvrius.tims.IOnStackChangeListener;
import org.wvrius.tims.TIMSEngine;

public class TSipStack implements IOnAccountChangeListener {

	private Account account;
	private SipStack stack = null;
	private SipProvider sipProvider = null;
	
	private HashSet<IOnStackChangeListener> listeners;
	
	public TSipStack(Account _account)
	{
		listeners = new HashSet<IOnStackChangeListener>();
		account = _account;
		account.registerOnAccountChangeListener(this);
	}
	
	public void init() throws SocketException, InvalidArgumentException, SipException
	{
		createStack();	
	}
	
	public SipProvider SipProvider()
	{
		return sipProvider;
	}
	
	public void registerStackChangeListener(IOnStackChangeListener listener)
	{
		listeners.add(listener);
	}
	
	public void removeStackChangeListener(IOnStackChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	private void createStack() throws SocketException, InvalidArgumentException, SipException
	{
	
		Properties properties = new Properties();
		
		if(stack != null)
		{
			TIMSEngine.RemoveMessageProcessor(account);
			
			stack.stop();
			Iterator iter = stack.getListeningPoints();
			while(iter.hasNext())
			{
				ListeningPoint lp = (ListeningPoint)iter.next();
				stack.deleteListeningPoint(lp);
			}
			
			if(sipProvider != null)
			{
				
				stack.deleteSipProvider(sipProvider);
			}
			
		}
		
		
		/* Proxy all requests */
		if(account.getDomainName().trim().length() != 0 || account.getProxyPort() != Transport.PORT_5060)
		{
			String proxyHost = account.getOutboundServer()+ ":"+account.getProxyPort();
			properties.setProperty("javax.sip.OUTBOUND_PROXY", proxyHost + "/" + Transport.protocolToString(account.getPreferedTransport()));
			
		}
		
		properties.setProperty("javax.sip.STACK_NAME", "tims_"+account.getID());
		properties.setProperty("javax.sip.AUTOMATIC_DIALOG_SUPPORT", "off");
		stack = TIMSEngine.getSipFactory().createSipStack(properties);	
		
		
		
		int port;
		Random random = new Random();
		port = 25000 + random.nextInt(20000);
		
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    ListeningPoint lp = stack.createListeningPoint(inetAddress.getHostAddress(), port++, ListeningPoint.UDP);
	                    if(sipProvider == null)
	                    {
	                    	sipProvider = stack.createSipProvider(lp);
	                    }
	                    else
	                    {
	                    	sipProvider.addListeningPoint(lp);
	                    }
	                }
	            }
	        }
	        
	        if(sipProvider == null)
	        {
	        	throw new RuntimeException();
	        }
	        
	        stack.start();
	        
	        TIMSEngine.CreateMessageProcessor(account);
	        notifyListeners();
		
	}
	
	private void notifyListeners()
	{
		for(IOnStackChangeListener listener : listeners)
			listener.OnStackChange(account);
	}
	
	@Override
	public void OnAccountChange(Account ac) {
		try {
			createStack();		
		} catch (PeerUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ObjectInUseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransportAlreadySupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
