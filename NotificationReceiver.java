package com.rockstar.medcab.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.rockstar.medcab.R;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "BloodDonationChannel";
    private static final String CHANNEL_NAME = "Blood Donation Notifications";
    private static final String CHANNEL_DESCRIPTION = "Receive notifications for blood donation";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create the notification channel (for Android 8.0 and above)
        createNotificationChannel(context);

        // Build the notification
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle("Blood Donation Reminder")
                    .setContentText("It's time to donate blood. Your contribution can save lives!")
                    .setAutoCancel(true);
        }

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
