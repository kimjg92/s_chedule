package com.shinhan.fcmexam;

/**
 * Created by GYU on 2017-03-25.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "MsgService";

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
        Log.d(TAG,s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);

        Log.d(TAG,s);
    }

    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String Title = "";
        String Body = "";
        //추가한것
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "message Type : " + remoteMessage.getMessageType());
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        } else{
            Log.d(TAG, "remoteMessage.getData is null");
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG,"Message Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG,"Message Notification TAG: " + remoteMessage.getNotification().getTag());
            Title = remoteMessage.getNotification().getTitle();
            Body = remoteMessage.getNotification().getBody();
        } else {
            Log.d(TAG,"remoteMessagee.getNotification is null");

        }

        sendNotification(remoteMessage.getData().get("message"),Body, Title);
    }

    private void sendNotification(String messageBody, String Body, String Title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(Title)
                .setContentText(Body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        //Log.d("FCM Debug",messageBody);
    }

}