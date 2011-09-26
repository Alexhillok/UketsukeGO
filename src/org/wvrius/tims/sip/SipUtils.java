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
package org.wvrius.tims.sip;

import gov.nist.javax.sip.SIPConstants;

import java.util.UUID;

public class SipUtils {
	
	public static String GenerateBranchId()
	{
		UUID uid = UUID.randomUUID();
		
		return SIPConstants.BRANCH_MAGIC_COOKIE_LOWER_CASE + uid.toString();
	}

}
