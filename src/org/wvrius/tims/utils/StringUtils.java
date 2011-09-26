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
package org.wvrius.tims.utils;

public class StringUtils {
	
	 //TODO - Change HEX transformation With Own Code
	 static final byte[] CHAR_TABLE = {
		    '0', '1', '2', '3',
		    '4', '5', '6', '7',
		    '8', '9', 'a', 'b',
		    'c', 'd', 'e', 'f'
		  };    

		  public static String HEX(byte[] input)  
		  {
		    byte[] hex = new byte[2 * input.length];
		    int index = 0;

		    for (byte b : input) {
		      int v = b & 0xFF;
		      hex[index++] = CHAR_TABLE[v >>> 4];
		      hex[index++] = CHAR_TABLE[v & 0xF];
		    }
		    
		    return new String(hex);
		  }


}
