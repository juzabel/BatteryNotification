/* 
 * Copyright 2014 Julián Zaragoza 
 * 
 * This file is part of BN: Battery Notification
 * 
 * BN: Battery Notification is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. 
 * You should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 */
package zarbel.bat.activities;

import zarbel.bat.services.BatteryService;
import zarbel.bat.R;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
/**
 * App Preferences Activity.
 * @author jzaragoza
 *
 */
public class PreferencesActivity extends PreferenceActivity{

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Adding prefereces file to activity
		addPreferencesFromResource(R.xml.defaultprefs);
		
		//Creating battery service
		Intent serviceIntent = new Intent(getApplicationContext(), BatteryService.class);
		startService(serviceIntent);

	}

	@Override
	protected void onStop() {

		//When the activity stops, we recreate the main service with new changes
		//Stop
		stopService(new Intent(PreferencesActivity.this, BatteryService.class));
		
		//Start
		Intent serviceIntent = new Intent(this, BatteryService.class);
		startService(serviceIntent);
		
		super.onStop();
	}


}
