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

import zarbel.bat.R;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * Service thread that manages the battery level update.
 * @author jzaragoza
 *
 */

@SuppressLint("NewApi")
public class BatteryThread extends Thread {

	private Notification notification; //Main notification to update with battery level.

	private boolean interrupted = false; //Flag to check if thread has been interrupted

	private Context applicationContext; //Service context

	private NotificationCompat.Builder mBuilder; //Battery level notification builder

	private NotificationManager mNotificationManager; //Notification Manager

	private PendingIntent nextPendingIntent; //Battery Notification pending intent
	private IntentFilter ifilter; //Intent filter to check battery level

	private Intent batteryStatusIntent; //Intent to receive battery level data
	private Intent nextIntent; //Battery Notification intent

	private long sleepTime; //Sleep time
	private SharedPreferences config; //Shared preferences

	public BatteryThread(Context context){

		//Saving creation context.
		this.applicationContext = context; 

		//Getting the user-defined sleep time
		config =  PreferenceManager.getDefaultSharedPreferences(context);

		//Getting user selected sleep time by preferences
		sleepTime = Long.valueOf(config.getString(context.getResources().getString(R.string.updates_interval), "300000"));

		//Creating intentFilter to get the battery level
		ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED); 

		//Getting the notification manager
		mNotificationManager =(NotificationManager) this.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

		createNotification();

	}

	private void createNotification() {
		//Creating the notification builder
		mBuilder =	new NotificationCompat.Builder(this.applicationContext);

		//Creating intent and pendingintent needed to show notification.
		nextIntent = new Intent();
		nextPendingIntent = PendingIntent.getBroadcast(applicationContext, 0, nextIntent, 0);
		mBuilder.setContentIntent(nextPendingIntent);

		//Setting notification as persistent
		mBuilder.setOngoing(true);
		mBuilder.setContentTitle(applicationContext.getResources().getString(R.string.app_name));
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
		notification = mBuilder.build();

		//Updating battery percentage
		setBatteryPercentage(applicationContext, ifilter, batteryStatusIntent,mBuilder, mNotificationManager);

		//Notify changes
		mNotificationManager.notify(BatteryService.SERVICE_ID, notification);

	}

	/**
	 * Thread interrupt action
	 */
	@Override
	public void interrupt() {
		interrupted = true;
		super.interrupt();
	}

	/**
	 * Static method to update battery level from screen on event
	 * @param applicationContext
	 */
	public void update(){
		setBatteryPercentage(applicationContext, ifilter, batteryStatusIntent, mBuilder, mNotificationManager);
		mNotificationManager.notify(BatteryService.SERVICE_ID, notification);

	}

	@Override
	public void run() {
		do{
			//If update on screen on is disabled, we unregister the Broadcast Receiver
			if(!config.getBoolean(applicationContext.getResources().getString(R.string.screenon), true)){
				((BatteryService)applicationContext).unregisterScreenReceiver();
			}else{
				((BatteryService)applicationContext).registerScreenReceiver();
			}
			//Updating battery percentage
			setBatteryPercentage(applicationContext, ifilter, batteryStatusIntent, mBuilder, mNotificationManager);

			//Notifying changes.
			mNotificationManager.notify(BatteryService.SERVICE_ID, notification);

			//Thread sleep action to save battery
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}while (!interrupted) ;
	}

	/**
	 * Thread start action
	 */
	@Override
	public synchronized void start() {
		super.start();
	}

	/**.drawable.ic_stat_name
	 * Setting the battery percentage level to the notification
	 * @param batteryStatusIntent
	 * @param level
	 * @param scale.drawable.ic_stat_name
	 * @param mBuildewr
	 * @param mNotificationManager
	 * @return
	 */
	private void setBatteryPercentage(Context applicationContext,
			IntentFilter ifilter, Intent batteryStatusIntent, NotificationCompat.Builder mBuilder,
			NotificationManager mNotificationManager) {

		int level = 0; //Level data, got from batteryStatusIntent
		int scale = 0; //Scale level, got from batteryStatusIntent
		float totalLevel = 0; //Total battery level
		
		//Getting battery level
		batteryStatusIntent = applicationContext.registerReceiver(null, ifilter);
		level = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		scale = batteryStatusIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		//Calculating battery level.
		totalLevel = level / (float)scale;
		int percent = ((int)(totalLevel*100));
		
		//Creating and Showing notification
		
		if(notification == null)
			createNotification();
		
		//Getting the corresponding image with the right battery digit.
		int idResource = applicationContext.getResources().getIdentifier(
				"ic_state_battery" + percent, "drawable",
				applicationContext.getPackageName());

		//Updating icon
		notification.icon = idResource;
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
			notification.largeIcon = BitmapFactory.decodeResource(applicationContext.getResources(),idResource);


	}
}
