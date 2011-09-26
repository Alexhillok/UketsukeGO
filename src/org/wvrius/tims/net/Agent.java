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

import org.wvrius.tims.IDisposable;

public abstract class Agent implements IDisposable {
	
	protected MessageProcessor mProcessor;
	
	
	public static enum AgentType
	{
		UNKOWN,
		REGISTER,
		MESSAGING
	}
	
	
	protected AgentType type  = AgentType.UNKOWN;
	
	public AgentType type()
	{
		return type;
	}
	
	public void stop(){
		
		
	};
	
	public void start(){};
	
	public void bindProcessor(MessageProcessor processor)
	{
		mProcessor = processor;
	}
	
	
	public void dispose(){}

}
