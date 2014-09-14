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
package zarbel.bat.services;

import zarbel.bat.broadcastreceivers.Autostart;
import zarbel.bat.R;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 * Main application thread
 * @author jzaragoza
 *
 */
public class BatteryService extends Service{

	public static final int SERVICE_ID = 1; //Service Id
	private static BatteryThread batteryThread; //Parallel thread that manages the battery level.
	private SharedPreferences config; //Application preferences
	private IntentFilter filter; //Intent filter for the broadcast receiver
	private BroadcastReceiver mReceiver; //Broadcast receiver for screen on event.
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	/**
	 * Stop service action
	 */
	@Override
	public boolean stopService(Intent name) {

		//If the service thread has been created before, we interrupt it.
		if(batteryThread != null){
			batteryThread.interrupt();
			batteryThread = null;
		}
		return super.stopService(name);
	}

	@Override
	public void onDestroy() {

		//If the lifecycle onDestroy method is called, we must ensure that the service thread stops too (If has been created)
		if(batteryThread != null){
			batteryThread.interrupt();
			batteryThread = null;
		}
		unregisterScreenReceiver();
		super.onDestroy();
	}

	/**
	 * Service creation action.
	 */
	@Override
	public void onCreate() {

		//Getting shared preferences
		config = PreferenceManager.getDefaultSharedPreferences(this);
		
		//We create and star the service thread
		batteryThread = new BatteryThread(this);
		batteryThread.start();

		//We have to create the intentfilter to detect the screen on event.
		filter = new IntentFilter(Intent.ACTION_SCREEN_ON); //Intent filter creation
		filter.addAction(Intent.ACTION_SCREEN_OFF); //Adding action

		//If update on screen-on is enabled...
		if(config.getBoolean(getResources().getString(R.string.screenon), true)){
			registerScreenReceiver();
		}
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY; //Forcing to restart.
	}

	public void unregisterScreenReceiver(){
		try{
			unregisterReceiver(mReceiver);
		}catch(Exception e){}
	}

	public void registerScreenReceiver() {
		mReceiver = new Autostart();//Creating BroadcastReceiver 
		registerReceiver(mReceiver, filter); //Adding filter to receiver, and register action		
	}
	//force Update
	public static void update(){
		try{
			batteryThread.update();
		}catch(Exception e){e.printStackTrace();}
	}
}
