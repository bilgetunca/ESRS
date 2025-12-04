package com.example.soscloud.ui.sospersonnel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.app.NotificationChannel;

import com.example.soscloud.R;
import com.example.soscloud.SosPersonnelActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM_Service";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message Data: " + remoteMessage.getData());
            // You can also handle data messages
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            if (title != null && body != null) {
                showNotification(title, body);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed FCM token: " + token);
        // Send token to your server if needed
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "default_channel";
            String channelName = "Acil Durum Bildirimleri";
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Acil durum bildirimi kanalı");
            manager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String body) {
        createNotificationChannel();
        Log.d(TAG, "Showing notification: " + title + " - " + body);

        // Create an intent for when user taps the notification
        Intent intent = new Intent(this, SosPersonnelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default_channel")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(new long[]{1000, 1000})
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        // Show the notification
        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(0, builder.build());
                Log.d(TAG, "Notification successfully displayed");
            } else {
                Log.w(TAG, "Notifications are disabled by the user.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage());
        }
    }
}