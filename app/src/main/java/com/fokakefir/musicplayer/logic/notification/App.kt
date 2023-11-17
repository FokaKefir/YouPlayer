package com.fokakefir.musicplayer.logic.notification;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    // region 0. Constants

    public static final String CHANNEL_ID_1 = "notification_channel1";
    public static final String CHANNEL_ID_2 = "notification_channel2";


    // endregion

    // region 1. Decl. and Init

    // endregion

    // region 2. Lifecycle

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    // endregion

    // region 3. Notification

    public void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_ID_1,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("This is notification channel 1");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }

    // endregion

}
