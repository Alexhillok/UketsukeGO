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
package org.wvrius.tims.sip.authentification;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import javax.sip.address.AddressFactory;
import javax.sip.header.AuthorizationHeader;
import javax.sip.header.Header;
import javax.sip.header.HeaderFactory;
import javax.sip.header.WWWAuthenticateHeader;

import org.wvrius.tims.utils.StringUtils;

public class DigestAuthentification {
		protected String realm;
		protected String username;
		protected String uri;
		protected String nonce;
		protected String password;
		protected String method;
		protected String cnonce;
		protected String qop;
		protected int qopValue;
	    protected int nc;
	    protected MessageDigest messageDigest;
	    protected String algo;
	    
	    public final int AUTH = 1;
	    public final int AUTH_INT = 2;
	    public final int UNSPECIFIED = 0;
	    
	    public static String GenerateNonce() {
			byte[] buffer = new byte[32];
			Random rand = new Random();
			for (int i = 0; i < 32; i++) {
				int n = rand.nextInt(25);
				buffer[i] = (byte) ((n % 2 == 0) ? 48 + (n % 10) : 97 + n);
			}
			return new String(buffer);
		}
	    
	    public DigestAuthentification()
	    {
	    	
	    }
	    
	    
	    public DigestAuthentification(WWWAuthenticateHeader wwwaheader, String uri, String method, String username, String password) throws Exception {
	        
	    	this.username=username;
            this.password=password;
            this.method = method;
            this.uri = uri;
            nonce = wwwaheader.getNonce();   
            realm = wwwaheader.getRealm();   
            qop = wwwaheader.getParameter("qop");   
            algo = wwwaheader.getAlgorithm();
            
            
            
            messageDigest = MessageDigest.getInstance(algo);
            
        	qopValue = UNSPECIFIED;
        	if(qop != null)
            {
        		String[] split = qop.split(",");
        		if(split != null)
        		{
        			for(String s: split)
        				if(s.equals("auth"))
        					qopValue |= AUTH;
        				else if(s.equals("auth-int"))
        					qopValue |= AUTH_INT;
        		}
        	}
        	
        	if(qopValue == AUTH_INT)
        	{
        		qop = "auth-int";
        		throw new RuntimeException("auth-int not implemented");
        	}

            if((qopValue & AUTH) != 0)
            {
                qop = "auth";
                qopValue = AUTH;
            }
        	
        	if(cnonce == null)
        	{
        		this.cnonce = GenerateNonce();
        		this.nc = 1;
        	}
    	}
	    
	    /*  
	      A1 = username:realm:password	     
	     */
	    
	    public String A1()
	    {
	    	return String.format("%s:%s:%s", username, realm, password);
	    }
	    
	    public String HA1()
	    {
	    	return StringUtils.HEX(messageDigest.digest(A1().getBytes()));
	    }
	   
	    /*  
	      if qop == "auth" or undefined
	      A2 = method:digestURI
	      if qop == "auth-int"
	      A2 = method:digestURI:MD5(body)     
	     */
	    
	    public String A2()
	    {
	    
	    	if(qopValue == AUTH_INT)
	    	{
	    		//TODO: AUTH-INT
	    	}
	    	else if(qopValue == UNSPECIFIED || (qopValue & AUTH) != 0)
	    	{
	    		return String.format("%s:%s", method, uri);
	    	}
	    	
	    	return null;
	    }
	    
	    public String HA2()
	    {
	    	return StringUtils.HEX(messageDigest.digest(A2().getBytes()));
	    }
	    
	    public Header getAuthorizationHeader(HeaderFactory headerFactory, AddressFactory addressFactory)
	    {
	    	try
	    	{
	    	 AuthorizationHeader header=headerFactory.createAuthorizationHeader("Digest");   
	    	 header.setAlgorithm(algo);
	    	 header.setCNonce(cnonce);
	    	 header.setNonce(nonce);
	    	 header.setNonceCount(nc);
	    	 header.setRealm(realm);
	    	 header.setURI(addressFactory.createURI(uri));
	    	 header.setUsername(username);
	    	 if(qop != null)
	    		 header.setQop(qop);
	    	 header.setResponse(this.getResponse());
	    	 
	    	 
	        	return header;
	    	} catch(Exception ex)
	    	{
	    		ex.printStackTrace();
	    	}
			return null;
	    }
	    
	    public String getResponse() {
	
	    	System.out.println("A1 = " + A1());
	    	System.out.println("HA1 = " + HA1());
	    	
	    	System.out.println("A2 = " + A2());
	    	System.out.println("HA2 = " + HA2());
	    	String A3 = "";
	    	
	    	if(qopValue != UNSPECIFIED)
	    	{
	    		A3 = String.format("%s:%s:%08d:%s:%s:%s",HA1(),nonce,nc,cnonce,qop,HA2());
	    	}
	    	else
	    	{
	    		A3 = String.format("%s:%s:%s", HA1(), nonce, HA2());
	    	}
	    	
	    	System.out.println("A3 = " + A3);
	    	
	    	 String response = StringUtils.HEX(messageDigest.digest(A3.getBytes()));
	    	 System.out.println("response = " + response);
		    	 	         
	         return response;
	    }
	    
	    public static void main(String[] args) {
		
	    	DigestAuthentification a = new DigestAuthentification();
			a.method = "REGISTER";
			a.password = "marius";
			a.realm = "testnet.ro";
			a.nonce = "91d7fe91ca7959827ca85cce4e9b0636";
			a.uri = "sip:testnet.ro";
			a.qop = "auth";
			a.nc = 1;
			a.cnonce = "0f7fed4012f248c0862ff40866c8c74d";
			a.username = "marius@testnet.ro";
			a.qopValue = a.AUTH;
			try {
				a.messageDigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				
			}
			
	

			
			String HA1 = a.HA1();
			System.out.println("HA1 is: " + HA1);
			
			
			String HA2 = a.HA2();
			System.out.println("HA2 "+ a.A2()  +" is: " + HA2);
			
			String response1 = a.getResponse();
			
			System.out.println("Response is: " + response1);
	

		}
}
