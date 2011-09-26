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

import org.wvrius.tims.Accounts;
import org.wvrius.tims.R;
import org.wvrius.tims.TIMSEngine;
import org.wvrius.tims.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;

public class TimsActivity extends Activity {
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        TIMSEngine.initialize(this);
    }
    
    public void showDialer(View view)
    {
    	MessageBox.ShowMessage(this, "Show dialer now");
    }
    
    public void showSettings(View view)
    {
    	Intent intent = new Intent(TimsActivity.this, org.wvrius.tims.ui.Settings.class);
        startActivity(intent);
    }
    
    public void sendMessage()
    {
    	
    }
}
