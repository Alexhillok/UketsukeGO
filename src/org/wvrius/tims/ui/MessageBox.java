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
package org.wvrius.tims.ui;
import android.content.Context;
import android.widget.Toast;


public class MessageBox {

	public static void ShowMessage(Context context, String message)
	{
		 Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
	}
	
}
