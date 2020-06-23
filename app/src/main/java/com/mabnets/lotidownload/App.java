package com.mabnets.lotidownload;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_1_ID="channel";
    @Override
    public void onCreate() {
        super.onCreate();
        createnotificationchannels();
    }
    private void createnotificationchannels(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel1=new NotificationChannel(
                    CHANNEL_1_ID,
                    "notification_channel one",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("you have a text message");

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

}
