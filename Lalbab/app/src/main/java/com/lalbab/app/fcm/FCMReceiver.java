package com.lalbab.app.fcm;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import com.lalbab.app.Config.BaseURL;
import com.lalbab.app.MainActivity;
import com.lalbab.app.R;


public class FCMReceiver extends FirebaseMessagingService {

    NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onCreate() {
        notificationManagerCompat = NotificationManagerCompat.from(this);
        super.onCreate();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {



        String title = "";
        String message = "";
        int id =0;

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        try {
            if (object.has("id"))
                id = Integer.parseInt(object.getString("id"));
            if (BaseURL.LANGUAGE.contains("ar")) {
                if (object.has("title"))
                    title = object.getString("title");
                if (object.has("message"))
                    message = object.getString("message");
            }if (BaseURL.LANGUAGE.contains("en")) {
                if (object.has("title_en"))
                    title = object.getString("title_en");
                if (object.has("message"))
                    message = object.getString("message");
            }if (BaseURL.LANGUAGE.contains("ku")) {
                if (object.has("title_ku"))
                    title = object.getString("title_ku");
                if (object.has("message_ku"))
                    message = object.getString("message_ku");
            }
            if (title.isEmpty() || message.isEmpty() ){
                if (object.has("title"))
                    title = object.getString("title");
                if (object.has("message"))
                    message = object.getString("message");
            }

        } catch (JSONException e) {
        e.printStackTrace();
    }

        Log.e("JSON_OBJECT", object.toString());
        Log.e("JSON_OBJECT","("+ remoteMessage.getData()+")");

        String NOTIFICATION_CHANNEL_ID = "MyNotifications";

        long pattern[] = {0, 1000, 500, 1000};

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);




      //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Lalbab Notifications",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(""+message);

            notificationChannel.enableLights(true);
           // notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
            NotificationChannel channel = mNotificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
            channel.canBypassDnd();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);




        PendingIntent contentIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);


        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
      //  Uri uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/raw/wake_up.mp3");

        notificationBuilder.setAutoCancel(true)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTitle(""+title)
                .setContentText(""+message)

                .setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(icon)
               // .setSound(uri)
                .setAutoCancel(true);




        mNotificationManager.notify(id, notificationBuilder.build());



        }


    @Override
    public void onDestroy() {
       // stopNotificationSound();
        super.onDestroy();

    }



}
