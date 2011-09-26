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

import org.wvrius.tims.net.Agent;
import org.wvrius.tims.net.RegisterAgent;

import android.widget.CompoundButton;

public class OnCheckedChangeListener implements
		android.widget.CompoundButton.OnCheckedChangeListener {

	private Account account;
	
	public OnCheckedChangeListener(Account ac)
	{
		account = ac;
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		RegisterAgent ra = (RegisterAgent) TIMSEngine.GetAgent(account, Agent.AgentType.REGISTER);
		if(ra != null)
		{
			if(isChecked)
				ra.register();
			else
				ra.unregister();
		}

	}

}
