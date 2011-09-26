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

public class RegisterState {

	public static enum State
	{
		 REGISTERING,
		 NOT_AUTHORIZED,
		 REGISTERED,
		 SERVER_ERROR,
		 UNREGISTERING,
		 NOT_REGISTERED
	}

	
}
