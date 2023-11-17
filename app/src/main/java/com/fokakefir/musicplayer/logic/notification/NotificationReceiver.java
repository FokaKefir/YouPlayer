package com.fokakefir.musicplayer.logic.notification;

import static com.fokakefir.musicplayer.gui.activity.MainActivity.*;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fokakefir.musicplayer.logic.player.MusicPlayerService;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String INTENT_FILTER_NOTIFICATION_BROADCAST = "data_notification_broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle args = intent.getExtras();
        switch (args.getString(MusicPlayerService.NOTIFICATION_EXTRA)) {
            case MusicPlayerService.NOTIFICATION_PLAY:
                playMusic(context);
                break;

            case MusicPlayerService.NOTIFICATION_PAUSE:
                pauseMusic(context);
                break;

            case MusicPlayerService.NOTIFICATION_PREVIOUS:
                previousMusic(context);
                break;

            case MusicPlayerService.NOTIFICATION_NEXT:
                nextMusic(context);
                break;
        }
    }

    public void playMusic(Context context) {
        Intent intent = new Intent(INTENT_FILTER_NOTIFICATION_BROADCAST);
        intent.putExtra(TYPE, INTENT_TYPE_PLAY);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void pauseMusic(Context context) {
        Intent intent = new Intent(INTENT_FILTER_NOTIFICATION_BROADCAST);
        intent.putExtra(TYPE, INTENT_TYPE_PAUSE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void previousMusic(Context context) {
        Intent intent = new Intent(INTENT_FILTER_NOTIFICATION_BROADCAST);
        intent.putExtra(TYPE, INTENT_TYPE_PREVIOUS);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public void nextMusic(Context context) {
        Intent intent = new Intent(INTENT_FILTER_NOTIFICATION_BROADCAST);
        intent.putExtra(TYPE, INTENT_TYPE_NEXT);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}
