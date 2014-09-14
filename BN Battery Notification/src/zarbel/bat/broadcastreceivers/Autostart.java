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
package zarbel.bat.broadcastreceivers;
import zarbel.bat.R;
import zarbel.bat.services.BatteryService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * BroadcastReceiver that updates battery level every screen on action.
 * @author jzaragoza
 *
 */

public class Autostart extends BroadcastReceiver {

	private SharedPreferences config;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		//Getting shared preferences
		config =  PreferenceManager.getDefaultSharedPreferences(arg0);

		//If autostart is enabled...
		if(config.getBoolean(arg0.getResources().getString(R.string.autostart), true)){

			boolean screenOn = config.getBoolean(arg0.getResources().getString(R.string.screenon), true);
			
			//If screen is on, and the screenOn option is enabled...
			if( screenOn && arg1.getAction().equals(Intent.ACTION_SCREEN_ON)){

				//We update the battery level
				BatteryService.update();

			//In other case...
			}else {

				//We start the main app service.
				Intent serviceIntent = new Intent(arg0, BatteryService.class);
				arg0.startService(serviceIntent);
			}
		}

	}

}
