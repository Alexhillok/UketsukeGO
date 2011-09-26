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

public class Transport {
	public static final int PROTOCOL_UDP = 1;
	public static final int PROTOCOL_TCP = 2;
	public static final int PROTOCOL_UNKNOWN = 0x666;
	public static final int PORT_5060 = 5060;
	
	public static int parseProtocol(String p)
	{
		if(p.toLowerCase().equals("udp"))
			return PROTOCOL_UDP;
		
		if(p.toLowerCase().equals("tcp"))
				return PROTOCOL_TCP;
			
		return PROTOCOL_UNKNOWN;
	}
	
	public static String protocolToString(int proto)
	{
		if(proto == PROTOCOL_TCP)
			return "TCP";
		
		if(proto == PROTOCOL_UDP)
			return "UDP";
		
		return null;
	}
	
}
