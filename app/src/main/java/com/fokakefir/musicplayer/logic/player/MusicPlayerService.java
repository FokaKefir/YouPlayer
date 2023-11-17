package com.fokakefir.musicplayer.logic.player;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.logic.notification.App;
import com.fokakefir.musicplayer.logic.notification.NotificationReceiver;

import static com.fokakefir.musicplayer.gui.activity.MainActivity.INTENT_FILTER_ACTIVITY;
import static com.fokakefir.musicplayer.logic.notification.NotificationReceiver.INTENT_FILTER_NOTIFICATION_BROADCAST;

public class MusicPlayerService extends Service implements MusicPlayer.MusicPlayerListener, Runnable {

    // region 0. Constants

    public static final String INTENT_FILTER_SERVICE = "data_service";

    public static final String INTENT_TYPE_PREPARED = "type_prepared";
    public static final String INTENT_TYPE_PLAY = "type_play";
    public static final String INTENT_TYPE_PAUSE = "type_pause";
    public static final String INTENT_TYPE_STOP = "type_stop";
    public static final String INTENT_TYPE_POSITION = "type_position";

    public static final String TYPE = "type";
    public static final String IMAGE_RESOURCE = "img_resource";
    public static final String TITLE = "title";
    public static final String ARTIST = "artist";
    public static final String LENGTH = "length";
    public static final String POSITION = "position";
    public static final String PLAYLIST_ID = "playlist_id";
    public static final String AUDIO_SESSION_ID = "audio_session_id";

    public static final int NOTIFICATION_ID = 1;
    public static final String NOTIFICATION_EXTRA = "notification_extra";
    public static final String NOTIFICATION_PREVIOUS = "previous";
    public static final String NOTIFICATION_NEXT = "next";
    public static final String NOTIFICATION_PLAY = "play";
    public static final String NOTIFICATION_PAUSE = "pause";

    // endregion

    // region 1. Decl and Init

    private MusicPlayer musicPlayer;

    private Handler handler;

    private NotificationManagerCompat notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationCompat.Builder notBuilderService;

    private MediaSessionCompat mediaSession;

    private IBinder binder = null;

    // endregion

    // region 2. Lifecycle


    @Override
    public void onCreate() {
        super.onCreate();
        NotificationChannel channel = new NotificationChannel(App.CHANNEL_ID_2, "PennSkanvTicChannel", NotificationManager.IMPORTANCE_MAX);
        channel.setDescription("MusicPlayer channel for foreground service notification");

        this.notificationManager = NotificationManagerCompat.from(this);
        this.notificationManager.createNotificationChannel(channel);
        this.notBuilderService = new NotificationCompat.Builder(this, App.CHANNEL_ID_2);

        prepareAndStartForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY_COMPATIBILITY;
        } else {
            this.musicPlayer = new MusicPlayer(this, this);

            LocalBroadcastManager.getInstance(this).registerReceiver(this.receiver, new IntentFilter(INTENT_FILTER_ACTIVITY));
            LocalBroadcastManager.getInstance(this).registerReceiver(this.receiver, new IntentFilter(INTENT_FILTER_NOTIFICATION_BROADCAST));

            this.handler = new Handler(Looper.getMainLooper());
            this.handler.post(this);

            this.mediaSession = new MediaSessionCompat(this, "media session");
        }
        return START_STICKY;
    }

    private void startForegroundServiceManually(Intent intent) {
        startService(intent);
    }

    private void prepareAndStartForeground() {
        try {
            Intent intent = new Intent(this, MusicPlayerService.class);
            startForegroundServiceManually(intent);

            startForeground(987, notBuilderService.build());
        } catch (Exception e) {
            Log.e(TAG, "startForegroundNotification: " + e.getMessage());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        stopForeground(true);
        return binder;
    }

    @Override
    public void onRebind(Intent intent) {
        stopForeground(true);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        prepareAndStartForeground();
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(this.receiver);
        deleteNotification();
    }

    // endregion

    // region 3. LocalBroadcastReceiver

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            switch (bundle.getString(MainActivity.TYPE)) {
                case MainActivity.INTENT_TYPE_PLAY_URI:
                    musicPlayer.setCurrentMusic(bundle.getParcelable(MainActivity.CURRENT_MUSIC));
                    musicPlayer.setMusics(bundle.getParcelableArrayList(MainActivity.MUSICS));
                    musicPlayer.setPlaylistId(bundle.getInt(MainActivity.PLAYLIST_ID));

                    Uri uri = Uri.parse(bundle.getString(MainActivity.URI));
                    musicPlayer.playMusicUri(uri);
                    break;

                case MainActivity.INTENT_TYPE_PLAY:
                    musicPlayer.playMusic();
                    break;

                case MainActivity.INTENT_TYPE_PAUSE:
                    musicPlayer.pauseMusic();
                    break;

                case MainActivity.INTENT_TYPE_NEXT:
                    musicPlayer.nextMusic();
                    break;

                case MainActivity.INTENT_TYPE_PREVIOUS:
                    musicPlayer.previousMusic();
                    break;

                case MainActivity.INTENT_TYPE_PROGRESS:
                    musicPlayer.setProgress(bundle.getInt(MainActivity.PROGRESS));
                    break;

                case MainActivity.INTENT_TYPE_INSERT_NEW_MUSIC:
                    musicPlayer.insertNewMusic(bundle.getParcelable(MainActivity.NEW_MUSIC));
                    break;

                case MainActivity.INTENT_TYPE_SHUFFLE:
                    musicPlayer.setShuffle(bundle.getBoolean(MainActivity.SHUFFLE));
                    break;

                case MainActivity.INTENT_TYPE_REPEAT:
                    musicPlayer.setRepeat(bundle.getBoolean(MainActivity.REPEAT));
                    break;

                case MainActivity.INTENT_TYPE_QUEUE_MUSIC:
                    musicPlayer.addQueueMusic(bundle.getParcelable(MainActivity.NEW_MUSIC));
                    break;
            }
        }
    };

    // endregion

    // region 4. Music listener

    @Override
    public void onPreparedMusic(int imgResource, String title, String artist, int length, int playlistId, int audioSessionId) {
        Intent intent = new Intent(INTENT_FILTER_SERVICE);
        intent.putExtra(TYPE, INTENT_TYPE_PREPARED);
        intent.putExtra(IMAGE_RESOURCE, imgResource);
        intent.putExtra(TITLE, title);
        intent.putExtra(ARTIST, artist);
        intent.putExtra(LENGTH, length);
        intent.putExtra(PLAYLIST_ID, playlistId);
        intent.putExtra(AUDIO_SESSION_ID, audioSessionId);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        createNotification(title, artist);
    }

    @Override
    public void onPlayMusic(int imgResource) {
        Intent intent = new Intent(INTENT_FILTER_SERVICE);
        intent.putExtra(TYPE, INTENT_TYPE_PLAY);
        intent.putExtra(IMAGE_RESOURCE, imgResource);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        updateNotification();
    }

    @Override
    public void onPauseMusic(int imgResource) {
        Intent intent = new Intent(INTENT_FILTER_SERVICE);
        intent.putExtra(TYPE, INTENT_TYPE_PAUSE);
        intent.putExtra(IMAGE_RESOURCE, imgResource);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        updateNotification();
    }

    @Override
    public void onStopMediaPlayer(int imgResource) {
        Intent intent = new Intent(INTENT_FILTER_SERVICE);
        intent.putExtra(TYPE, INTENT_TYPE_STOP);
        intent.putExtra(IMAGE_RESOURCE, imgResource);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        deleteNotification();
    }

    // endregion

    // region 5. Runnable

    @Override
    public void run() {
        if (this.musicPlayer.isPlayable()) {
            int position = this.musicPlayer.getTimePosition();

            Intent intent = new Intent(INTENT_FILTER_SERVICE);
            intent.putExtra(TYPE, INTENT_TYPE_POSITION);
            intent.putExtra(POSITION, position);

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
        this.handler.postDelayed(this, 250);
    }

    // endregion

    // region 6. Notification

    @SuppressLint("UnspecifiedImmutableFlag")
    public void createNotification(String musicTitle, String musicArtist) {
        Intent broadcastIntentPrevious = new Intent(this, NotificationReceiver.class);
        broadcastIntentPrevious.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PREVIOUS);

        Intent broadcastIntentNext = new Intent(this, NotificationReceiver.class);
        broadcastIntentNext.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_NEXT);

        Intent broadcastIntentPlay = new Intent(this, NotificationReceiver.class);
        if (this.musicPlayer.isPlaying()) {
            broadcastIntentPlay.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PAUSE);
        } else {
            broadcastIntentPlay.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PLAY);
        }

        Bitmap artwork = BitmapFactory.decodeResource(getResources(), R.raw.ic_sound);

        this.notificationBuilder = new NotificationCompat.Builder(this, App.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_baseline_music_24)
                .setContentTitle(musicTitle)
                .setContentText(musicArtist)
                .setShowWhen(false)
                .setColor(Color.RED)
                .setLargeIcon(artwork)
                .addAction(R.drawable.ic_baseline_previous_24, "previous",
                        PendingIntent.getBroadcast(this, 0, broadcastIntentPrevious, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction((this.musicPlayer.isPlaying() ? R.drawable.ic_baseline_pause_music : R.drawable.ic_baseline_play_music), "play",
                        PendingIntent.getBroadcast(this, 1, broadcastIntentPlay, PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(R.drawable.ic_baseline_next_24, "next",
                        PendingIntent.getBroadcast(this, 2, broadcastIntentNext, PendingIntent.FLAG_UPDATE_CURRENT))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(this.mediaSession.getSessionToken()))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setOngoing(true);

        Notification notification = this.notificationBuilder.build();

        this.notificationManager.notify(NOTIFICATION_ID, notification);
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    public void updateNotification() {
        Intent broadcastIntentPrevious = new Intent(this, NotificationReceiver.class);
        broadcastIntentPrevious.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PREVIOUS);

        Intent broadcastIntentNext = new Intent(this, NotificationReceiver.class);
        broadcastIntentNext.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_NEXT);

        Intent broadcastIntentPlay = new Intent(this, NotificationReceiver.class);
        if (this.musicPlayer.isPlaying()) {
            broadcastIntentPlay.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PAUSE);
        } else {
            broadcastIntentPlay.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PLAY);
        }

        this.notificationBuilder.clearActions();
        this.notificationBuilder.addAction(R.drawable.ic_baseline_previous_24, "previous",
                PendingIntent.getBroadcast(this, 0, broadcastIntentPrevious, PendingIntent.FLAG_UPDATE_CURRENT));
        this.notificationBuilder.addAction((this.musicPlayer.isPlaying() ? R.drawable.ic_baseline_pause_music : R.drawable.ic_baseline_play_music), "play",
                        PendingIntent.getBroadcast(this, 1, broadcastIntentPlay, PendingIntent.FLAG_UPDATE_CURRENT));
        this.notificationBuilder.addAction(R.drawable.ic_baseline_next_24, "next",
                        PendingIntent.getBroadcast(this, 2, broadcastIntentNext, PendingIntent.FLAG_UPDATE_CURRENT));

        Notification notification = this.notificationBuilder.build();

        this.notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public void deleteNotification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    // endregion

}