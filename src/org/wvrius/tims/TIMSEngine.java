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

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;

import javax.sip.ListeningPoint;
import javax.sip.SipFactory;
import javax.sip.SipProvider;
import javax.sip.SipStack;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;

import org.wvrius.tims.net.Agent;
import org.wvrius.tims.net.Agent.AgentType;
import org.wvrius.tims.net.MessageProcessor;
import org.wvrius.tims.net.RegisterAgent;
import org.wvrius.tims.net.TSipStack;
import org.wvrius.tims.net.Transport;

import android.content.Context;

public class TIMSEngine {

	private static Context mainContext;
	private static SipFactory sipFactory = null;
	private static HeaderFactory headerFactory = null;
	private static AddressFactory addressFactory = null;
	private static MessageFactory messageFactory = null;
	private static HashMap<String, TSipStack> accountsSipStack = null;
	private static HashMap<String, HashMap<AgentType,Agent>> accountAgents = null;
	private static HashMap<String, MessageProcessor> accountMessageProcessor = null;
	public final static String UserAgent = "TIMS";
	public final static String VERSION = "0.1";
	public static IOnAccountsChangeListener accountsChangeListener = null;
	
	
	public static void initialize(Context _mainContext)
	{
		mainContext = _mainContext;
		accountsSipStack = new HashMap<String, TSipStack>();
		accountAgents = new HashMap<String, HashMap<AgentType, Agent>>();
		accountMessageProcessor = new HashMap<String, MessageProcessor>();
		Accounts.initialize();
		accountsChangeListener = new IOnAccountsChangeListener() {
			
			@Override
			public void OnAccountsChange() {
				// TODO Auto-generated method stub
				
			}
		};
		
		sipFactory = SipFactory.getInstance();
		//sipFactory.setPathName("gov.nist");
		
		try {
			
			headerFactory = sipFactory.createHeaderFactory();
			addressFactory = sipFactory.createAddressFactory();
			messageFactory = sipFactory.createMessageFactory();
			
			for(Account ac : Accounts.getDatabaseAccounts())
			{
					TSipStack stack = new TSipStack(ac);
					accountsSipStack.put(ac.getUID(), stack);
					
					/* Create agents */
					Agent registerAgent = new RegisterAgent(ac);
					AddAccountAgent(ac, registerAgent);	
					
					stack.registerStackChangeListener(new IOnStackChangeListener() {
						
						@Override
						public void OnStackChange(Account ac) {
							
							/* Get the new message processor*/
							MessageProcessor processor = GetMessageProcessor(ac);
							
							/* Update and start agents */	
							HashMap<AgentType, Agent> agents = TIMSEngine.GetAccountAgents(ac);
							if(agents != null)
								for(Agent ag : agents.values())
								{
									ag.stop();
									ag.bindProcessor(processor);
									ag.start();
								}
											
							
						}
					});
					stack.init();	    	
			}
			
		} catch (Exception e) {		
			e.printStackTrace();
		}
 		
	}
	
	public static HashMap<AgentType, Agent> GetAccountAgents(Account ac)
	{
		return accountAgents.get(ac.getUID());
	}
	
	public static void RemoveAllAgents(Account ac)
	{
		HashMap<AgentType,Agent> agents = accountAgents.get(ac.getUID());
		if(agents != null)
		{
			for(Agent ag : agents.values())
				ag.dispose();
								
		}
		
		accountAgents.remove(ac.getUID());
	}
	
	public static Agent GetAgent(Account ac, AgentType at)
	{
		HashMap<AgentType,Agent> agents = accountAgents.get(ac.getUID());
		if(agents == null)
			return null;
		
		return agents.get(at);
	}
	
	public static void AddAccountAgent(Account ac, Agent ag)
	{
		HashMap<AgentType,Agent> agents = accountAgents.get(ac.getUID());
		if(agents == null)
			agents = new HashMap<AgentType,Agent>();
		
		agents.put(ag.type(), ag);
		accountAgents.put(ac.getUID(), agents);
	}
	
	public static void RemoveMessageProcessor(Account ac)
	{
		MessageProcessor ma = accountMessageProcessor.get(ac.getUID());
		if(ma != null)
			ma.dispose();
		
		accountMessageProcessor.remove(ac.getUID());
	}
	
	public static MessageProcessor GetMessageProcessor(Account ac)
	{
		MessageProcessor ma = accountMessageProcessor.get(ac.getUID());
		return ma;
	}
	
	
	public static void CreateMessageProcessor(Account ac)
	{
		MessageProcessor ra = accountMessageProcessor.get(ac.getUID());
		if(ra == null)
		{
			accountMessageProcessor.put(ac.getUID(), new MessageProcessor(ac));
		}
	}
	
	public static SipProvider getAccountSipProvider(Account ac)
	{
		TSipStack stack = accountsSipStack.get(ac.getUID());
		return stack.SipProvider();
	}
	
	public static AddressFactory getAddressFactory()
	{
		return addressFactory;
	}
	
	public static MessageFactory getMessageFactory()
	{
		return messageFactory;
	}
	
	public static HeaderFactory getHeaderFactory()
	{
		return headerFactory;
	}
	
	public static SipFactory getSipFactory()
	{
		return sipFactory;
	}
	
	/**
	 * @return the mainContext
	 */
	public static Context getMainContext() {
		return mainContext;
	}


	
	
}
