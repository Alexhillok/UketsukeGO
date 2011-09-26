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

import gov.nist.javax.sip.SIPConstants;
import gov.nist.javax.sip.TransactionExt;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.message.SIPMessage;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TooManyListenersException;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogTerminatedEvent;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.SipListener;
import javax.sip.SipProvider;
import javax.sip.TimeoutEvent;
import javax.sip.Transaction;
import javax.sip.TransactionTerminatedEvent;
import javax.sip.TransactionUnavailableException;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.address.URI;
import javax.sip.header.AllowHeader;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.ExpiresHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.UserAgentHeader;
import javax.sip.header.ViaHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

import org.wvrius.tims.Account;
import org.wvrius.tims.IOnRegisterStatusChangeListener;
import org.wvrius.tims.TIMSEngine;
import org.wvrius.tims.sip.RegisterState;
import org.wvrius.tims.sip.RegisterState.State;
import org.wvrius.tims.sip.SipUtils;
import org.wvrius.tims.sip.authentification.DigestAuthentification;
import org.wvrius.tims.ui.TimsActivity;

import android.util.Log;

/**
 * @author marius
 * 
 */
public class RegisterAgent extends Agent implements SipListener {


	private static final int MAX_RETRIES = 3;

	private RegisterState.State state = State.NOT_REGISTERED;
	private AddressFactory addressFactory;
	private MessageFactory messageFactory;
	private HeaderFactory headerFactory;
	private Account account;
	private int retries = 0;
	String domainName;
	private HashSet<IOnRegisterStatusChangeListener> listeners;
	private final static int DEFAULT_REGISTRATION_TIME = 600;
	private int expires;

	
	public RegisterAgent(Account _account) {
		type = AgentType.REGISTER;
		account = _account;
		addressFactory = TIMSEngine.getAddressFactory();
		messageFactory = TIMSEngine.getMessageFactory();
		headerFactory = TIMSEngine.getHeaderFactory();
		listeners = new HashSet<IOnRegisterStatusChangeListener>();
		
		domainName = account.getDomainName().trim().length() == 0 ? account
				.getOutboundServer() : account.getDomainName();
				
		
	}
	
	@Override
	public void start() {
		
		mProcessor.registerMessageSipListener(Request.REGISTER, this);
		if (account.RegisterOnStart())
			register();
		
	}
	
	
	public static AgentType MyType()
	{
		return AgentType.REGISTER;
	}
	
	@Override
	public void dispose()
	{
		if(mProcessor != null)
			mProcessor.unregisterMessageSipListener(Request.REGISTER, this);
		listeners.clear();
	}
	
	public State getState()
	{
		return this.state;
	}

	public Boolean isRegistered() {
		return state == State.REGISTERED;
	}

	public void register() {
	
			synchronized (state) {
			
				if(this.state == State.REGISTERED || this.state == State.REGISTERED)
					return;
				
				Request requestMessage = getRegisterRequest(DEFAULT_REGISTRATION_TIME);
				expires = DEFAULT_REGISTRATION_TIME;
				this.retries = 0;
				setState(State.REGISTERING);
				sendRegisterRequest(requestMessage);
				
			}
			
		
	}
	
	public void unregister()
	{
		synchronized (state) {
			Request requestMessage = getRegisterRequest(0);
			setState(State.UNREGISTERING);
			this.retries = 0;
			sendRegisterRequest(requestMessage);
		}
		
	}
	
	public void setState(RegisterState.State state)
	{
		if(this.state != state)
		{
			this.state = state;	
			notifyChangeListeners();
		}
	}

	private void sendRegisterRequest(Request request) {
		if (retries++ <= MAX_RETRIES) {
				mProcessor.sendWithTransaction(request);	
		}
	}

	public Request getRegisterRequest(int expireTime) {

		try {
			SipProvider sipProvider;
			Request request;
			
			sipProvider = TIMSEngine.getAccountSipProvider(account);
			
			SipURI from = addressFactory.createSipURI(account.getUserName(),
					domainName);

			Address fromNameAddress = addressFactory.createAddress(from);
			fromNameAddress.setDisplayName(account.getDisplayName());

			FromHeader fromHeader = headerFactory.createFromHeader(
					fromNameAddress, "sipstag");

			SipURI toAddress = addressFactory.createSipURI(
					account.getUserName(), domainName);
			Address toNameAddress = addressFactory.createAddress(toAddress);
			toNameAddress.setDisplayName(account.getUserName());
			ToHeader toHeader = headerFactory.createToHeader(toNameAddress,
					null);

			URI requestURI = addressFactory.createURI("sip:" + domainName);

			ArrayList<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
			ViaHeader viaHeader = headerFactory.createViaHeader(sipProvider
					.getListeningPoint().getIPAddress(), sipProvider
					.getListeningPoint().getPort(), "udp", SipUtils.GenerateBranchId());
			viaHeaders.add(viaHeader);

			CallIdHeader callIdHeader = sipProvider.getNewCallId();

			CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1l,
					Request.REGISTER);

			MaxForwardsHeader maxForwards = headerFactory
					.createMaxForwardsHeader(70);

			SipURI contactURI = addressFactory.createSipURI(account
					.getUserName(), sipProvider.getListeningPoint()
					.getIPAddress());
			contactURI.setPort(sipProvider.getListeningPoint().getPort());
			Address contactAddress = addressFactory.createAddress(contactURI);
			contactAddress.setDisplayName(account.getDisplayName());
			ContactHeader contactHeader = headerFactory
					.createContactHeader(contactAddress);

			AllowHeader allowHeader = headerFactory
					.createAllowHeader("INVITE,ACK,BYE,CANCEL,OPTIONS,PRACK,REFER,NOTIFY,SUBSCRIBE,INFO,MESSAGE");

			List<String> userAgent = new LinkedList<String>();
			userAgent.add(TIMSEngine.UserAgent);
			userAgent.add(TIMSEngine.VERSION);

			UserAgentHeader uah = headerFactory
					.createUserAgentHeader(userAgent);

			ExpiresHeader exHeader = headerFactory.createExpiresHeader(expireTime);
			
			request = messageFactory.createRequest(requestURI,
					Request.REGISTER, callIdHeader, cSeqHeader, fromHeader,
					toHeader, viaHeaders, maxForwards);
			
			
			request.addHeader(contactHeader);
			request.addHeader(allowHeader);			
			request.addHeader(uah);
			request.addHeader(exHeader);

			return request;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

	@Override
	public void processDialogTerminated(DialogTerminatedEvent arg0) {

	}

	@Override
	public void processIOException(IOExceptionEvent arg0) {
	}

	@Override
	public void processRequest(RequestEvent arg0) {
	}

	/**
	 * @param trans - The transaction that contains the base request 401
	 * @param resp - The response to the request
	 */
	private void registerWithWWWAuthorizationHeader(Transaction t, Response resp) {

		try {
			
			Request request = t.getRequest();		
			Request clonedRequest = (Request)request.clone();
			
			CSeqHeader cseqHeader = (CSeqHeader) clonedRequest.getHeader(CSeqHeader.NAME);
			cseqHeader.setSeqNumber(cseqHeader.getSeqNumber() + 1);
			clonedRequest.setHeader(cseqHeader);
			
			
			ViaHeader viaHeader = (ViaHeader) clonedRequest.getHeader(ViaHeader.NAME);
			viaHeader.setBranch(SipUtils.GenerateBranchId());
			clonedRequest.setHeader(viaHeader);
			
			String method = cseqHeader.getMethod();
			
			WWWAuthenticateHeader authHeader = (WWWAuthenticateHeader) resp
					.getHeader(WWWAuthenticateHeader.NAME);

			DigestAuthentification digestAuthentification = new DigestAuthentification(
					authHeader, "sip:" + domainName, method,
					account.getAuthorizationName(), account.getPassword());

			clonedRequest.addHeader(digestAuthentification.getAuthorizationHeader(
					headerFactory, addressFactory));
			sendRegisterRequest(clonedRequest);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@Override
	public void processResponse(ResponseEvent responseEvent) {
		try {

			Response response = responseEvent.getResponse();
			switch (response.getStatusCode()) {
			case 401: {
				ClientTransaction transaction = responseEvent
						.getClientTransaction();
				registerWithWWWAuthorizationHeader(transaction, response);
			}
				break;
			case 407: {
				// TODO: Implement Proxy Authorization
				setState(State.NOT_AUTHORIZED);
			}
			break;
			case 403: {
				setState(State.NOT_AUTHORIZED);
			}
			break;
			case 200:
			{
				if(state == State.REGISTERING)
				{
					setState(State.REGISTERED);
				} else if(state == State.UNREGISTERING)
				{
					setState(State.NOT_REGISTERED);
				}
				
			}
			break;
			case 500:
			{
				setState(State.SERVER_ERROR);
			}
			break;
			default:
			{
				setState(State.NOT_AUTHORIZED);
			}
			break;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void processTimeout(TimeoutEvent arg0) {

	}

	@Override
	public void processTransactionTerminated(TransactionTerminatedEvent arg0) {

	}
	
	public void registerChangeListener(IOnRegisterStatusChangeListener listener)
	{
		listeners.add(listener);
	}
	
	private void notifyChangeListeners()
	{
		for(IOnRegisterStatusChangeListener listener : listeners)
		{
			listener.registerStateChanged(account, this.state);
		}
	}

}
