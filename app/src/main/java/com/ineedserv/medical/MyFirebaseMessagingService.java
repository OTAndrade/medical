package com.ineedserv.medical;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by andrade on 18-09-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = "NOTICIAS";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String from = remoteMessage.getFrom();
        Log.d(TAG, "Mensaje recibido de:"+from);

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token:" + token);

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG,"Notificacion"+remoteMessage.getNotification().getBody());
            mostrarNotificacion(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }

        if (remoteMessage.getData().size() >0 ){
            Log.d(TAG,"Data:  "+remoteMessage.getData());
        }

    }

    private void mostrarNotificacion(String title, String body) {
        Intent intent = new Intent(this, PrincipalActivity.class);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.alert_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}
