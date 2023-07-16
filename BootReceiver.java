package com.rockstar.medcab.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // TODO: Add your code to handle actions after device boot
            // For example, schedule a notification after 24 hours from the current time

            // Get the current time
            Calendar calendar = Calendar.getInstance();
            long currentTimeMillis = calendar.getTimeInMillis();

            // Set the notification time after 24 hours
            calendar.add(Calendar.HOUR_OF_DAY, 24);
            long notificationTimeMillis = calendar.getTimeInMillis();

            // Calculate the delay for the notification
            long delayMillis = notificationTimeMillis - currentTimeMillis;

            // Schedule the notification using AlarmManager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent notificationIntent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayMillis, pendingIntent);
        }
    }
}
