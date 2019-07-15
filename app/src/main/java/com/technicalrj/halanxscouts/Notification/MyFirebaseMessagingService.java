package com.technicalrj.halanxscouts.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.technicalrj.halanxscouts.Home.ScoutAcceptanceActivity;
import com.technicalrj.halanxscouts.HomeActivity;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;


import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "InfoText";
    public int Unique_Integer_Number = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    Profile profile;


    public MyFirebaseMessagingService() {

        Log.i(TAG,"MyFirebaseMessagingService");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());



        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            String data = remoteMessage.getData().get("data");

            Log.d(TAG, "Message data payload correct: " + data);
            Log.d(TAG, "Data Payload full: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(data);
                if(json.getJSONObject("category").getString("name").equals("NewTask")){
                    handleTaskNotification(json);
                }else {
                    handleDataMessage(json);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception123:" + e);
            }
        }



        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "ChatHistory Body: " + remoteMessage.getNotification().getBody());

            String sd =  "{\"data\":{\"title\":\"Payment\",\"content\":\"Helo\",\"category\":{\"name\":\"Payment\",\"image\":\"https:\\/\\/d3agek5ajs3os7.cloudfront.net\\/media\\/public\\/notification-category-images\\/1\\/nMHCi-chatbot.png\"},\"payload\":{}}}";
            try {
                JSONObject json = new JSONObject(sd);
                handleDataMessage(json);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            //handleNotification(remoteMessage.getNotification().getBody());
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }





    @Override
    public void onNewToken(String token) {
        Log.d("InfoText", "Refreshed token: " + token);

        // Saving reg id to shared preferences
        storeRegIdInPref(token);




    }





    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("firebase_id", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("gcm_id", token);
        editor.apply();
    }


    private void handleTaskNotification(JSONObject json){


        try {
            JSONObject payload = json.getJSONObject("payload");
            int taskId = payload.getInt("id");
            String dateTime = payload.getString("scheduled_at");
            String taskname = payload.getJSONObject("category").getString("name");

            String[] parts = dateTime.split(" ");



            Intent intent = new Intent(getApplicationContext(), ScoutAcceptanceActivity.class);
            String time=parts[3]+" "+parts[4];
            String date=parts[0] +" " + parts[1];
            intent.putExtra("id",taskId);
            intent.putExtra("date",date);
            intent.putExtra("time",time);
            intent.putExtra("task_name",taskname);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }






    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        String title="",content="",imageUrl="";
        try {
            title = json.getJSONObject("category").getString("name");
            content = json.getString("content");
            imageUrl = json.getJSONObject("category").getString("image");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder;

        Log.i(TAG,"Build.VERSION.SDK_INT:"+Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.i(TAG," Build.VERSION_CODES.O");
            CharSequence name = "noti_channel";
            String description = "noti_channel_desc";;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("noti_id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, channel.getId());

        }else {
            builder = new NotificationCompat.Builder(this );
        }
        //final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/notification");
        //Uri alarmSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        builder.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(Unique_Integer_Number, builder.build());


        try {
            Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notification);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }




}
