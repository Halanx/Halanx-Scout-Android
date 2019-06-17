package com.technicalrj.halanxscouts.Notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.JsonObject;
import com.technicalrj.halanxscouts.HomeActivity;
import com.technicalrj.halanxscouts.Notification.NoficationPojo.Notification;
import com.technicalrj.halanxscouts.Profile.ProfilePojo.Profile;
import com.technicalrj.halanxscouts.R;
import com.technicalrj.halanxscouts.RetrofitAPIClient;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Date;
import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String TAG = "InfoText";
    public int Unique_Integer_Number = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
    Profile profile;

    public MyFirebaseMessagingService() {

        Log.i(TAG,"MyFirebaseMessagingService");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());



        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("InfoText", "Message data payload: " + remoteMessage.getData());

            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            String data = remoteMessage.getData().toString();
            try {
                JSONObject json = new JSONObject(String.valueOf(data));
                handleDataMessage(json);
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


    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        }else{
            // If the app is in background, firebase itself handles the notification
        }
    }




    @Override
    public void onNewToken(String token) {
        Log.d("InfoText", "Refreshed token: " + token);

        // Saving reg id to shared preferences
        storeRegIdInPref(token);

        // sending reg id to your server
        sendRegistrationToServer(token);


    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);

        final SharedPreferences prefs = getApplicationContext().getSharedPreferences("login_user_halanx_scouts", MODE_PRIVATE);
        final String key = prefs.getString("login_key", null);


        final RetrofitAPIClient.DataInterface retrofitAPIClient = RetrofitAPIClient.getClient().create(RetrofitAPIClient.DataInterface.class);
        Call<Profile> call2 = retrofitAPIClient.updateProfileGcmId(token,"Token "+key);
        call2.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {

            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.i(TAG,t.getMessage());
                t.printStackTrace();
            }
        });



    }



    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("firebase_id", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        Log.i("InfoText","Token:"+token);
        editor.apply();
    }


    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        String title="",content="",imageUrl="";
        try {
            title = json.getJSONObject("data").getString("title");
            content = json.getJSONObject("data").getString("content");
            imageUrl = json.getJSONObject("data").getJSONObject("category").getString("image");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/raw/notification");
        builder.setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(Unique_Integer_Number, builder.build());






    }




}
