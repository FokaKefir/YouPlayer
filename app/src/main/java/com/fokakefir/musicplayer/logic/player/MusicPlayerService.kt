package com.fokakefir.musicplayer.logic.player

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.activity.MainActivity
import com.fokakefir.musicplayer.logic.notification.App
import com.fokakefir.musicplayer.logic.notification.NotificationReceiver
import com.fokakefir.musicplayer.logic.player.MusicPlayer.MusicPlayerListener

class MusicPlayerService : Service(), MusicPlayerListener, Runnable {
    // region 1. Decl and Init
    private var musicPlayer: MusicPlayer? = null
    private var handler: Handler? = null
    private var notificationManager: NotificationManagerCompat? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notBuilderService: NotificationCompat.Builder? = null
    private var mediaSession: MediaSessionCompat? = null
    private val binder: IBinder? = null

    // endregion

    // region 2. Lifecycle
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            App.CHANNEL_ID_2,
            "ServiceChannel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "MusicPlayer channel for foreground service notification"
        notificationManager = NotificationManagerCompat.from(this)
        notificationManager!!.createNotificationChannel(channel)
        notBuilderService = NotificationCompat.Builder(this, App.CHANNEL_ID_2)
        prepareAndStartForeground()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        musicPlayer = MusicPlayer(this, this)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(MainActivity.INTENT_FILTER_ACTIVITY))
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver,
            IntentFilter(NotificationReceiver.INTENT_FILTER_NOTIFICATION_BROADCAST)
        )
        handler = Handler(Looper.getMainLooper())
        handler!!.post(this)
        mediaSession = MediaSessionCompat(this, "media session")
        return START_STICKY
    }

    private fun startForegroundServiceManually(intent: Intent) {
        startService(intent)
    }

    private fun prepareAndStartForeground() {
        try {
            val intent = Intent(this, MusicPlayerService::class.java)
            startForegroundServiceManually(intent)
            startForeground(987, notBuilderService!!.build())
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "startForegroundNotification: " + e.message)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        stopForeground(true)
        return binder
    }

    override fun onRebind(intent: Intent) {
        stopForeground(true)
    }

    override fun onUnbind(intent: Intent): Boolean {
        prepareAndStartForeground()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayer?.stopMediaPlayer()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        deleteNotification()
    }

    // endregion
    // region 3. LocalBroadcastReceiver
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            when (bundle!!.getString(MainActivity.TYPE)) {
                MainActivity.INTENT_TYPE_PLAY_URI -> {
                    musicPlayer!!.currentMusic = bundle.getParcelable(MainActivity.CURRENT_MUSIC)
                    musicPlayer!!.setMusics(bundle.getParcelableArrayList(MainActivity.MUSICS))
                    musicPlayer!!.setPlaylistId(bundle.getInt(MainActivity.PLAYLIST_ID))
                    val uri = Uri.parse(bundle.getString(MainActivity.URI))
                    musicPlayer!!.playMusicUri(uri)
                }

                MainActivity.INTENT_TYPE_PLAY -> musicPlayer!!.playMusic()
                MainActivity.INTENT_TYPE_PAUSE -> musicPlayer!!.pauseMusic()
                MainActivity.INTENT_TYPE_NEXT -> musicPlayer!!.nextMusic()
                MainActivity.INTENT_TYPE_PREVIOUS -> musicPlayer!!.previousMusic()
                MainActivity.INTENT_TYPE_PROGRESS -> musicPlayer!!.setProgress(
                    bundle.getInt(
                        MainActivity.PROGRESS
                    )
                )

                MainActivity.INTENT_TYPE_INSERT_NEW_MUSIC -> musicPlayer!!.insertNewMusic(
                    bundle.getParcelable(
                        MainActivity.NEW_MUSIC
                    )
                )

                MainActivity.INTENT_TYPE_SHUFFLE -> musicPlayer!!.setShuffle(
                    bundle.getBoolean(
                        MainActivity.SHUFFLE
                    )
                )

                MainActivity.INTENT_TYPE_REPEAT -> musicPlayer!!.isRepeat =
                    bundle.getBoolean(MainActivity.REPEAT)

                MainActivity.INTENT_TYPE_QUEUE_MUSIC -> musicPlayer!!.addQueueMusic(
                    bundle.getParcelable(
                        MainActivity.NEW_MUSIC
                    )!!
                )
            }
        }
    }

    // endregion
    // region 4. Music listener
    override fun onPreparedMusic(
        imgResource: Int,
        title: String?,
        artist: String?,
        length: Int,
        playlistId: Int,
        audioSessionId: Int
    ) {
        val intent = Intent(INTENT_FILTER_SERVICE)
        intent.putExtra(TYPE, INTENT_TYPE_PREPARED)
        intent.putExtra(IMAGE_RESOURCE, imgResource)
        intent.putExtra(TITLE, title)
        intent.putExtra(ARTIST, artist)
        intent.putExtra(LENGTH, length)
        intent.putExtra(PLAYLIST_ID, playlistId)
        intent.putExtra(AUDIO_SESSION_ID, audioSessionId)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        createNotification(title, artist)
    }

    override fun onPlayMusic(imgResource: Int) {
        val intent = Intent(INTENT_FILTER_SERVICE)
        intent.putExtra(TYPE, INTENT_TYPE_PLAY)
        intent.putExtra(IMAGE_RESOURCE, imgResource)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        updateNotification()
    }

    override fun onPauseMusic(imgResource: Int) {
        val intent = Intent(INTENT_FILTER_SERVICE)
        intent.putExtra(TYPE, INTENT_TYPE_PAUSE)
        intent.putExtra(IMAGE_RESOURCE, imgResource)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        updateNotification()
    }

    override fun onStopMediaPlayer(imgResource: Int) {
        val intent = Intent(INTENT_FILTER_SERVICE)
        intent.putExtra(TYPE, INTENT_TYPE_STOP)
        intent.putExtra(IMAGE_RESOURCE, imgResource)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        deleteNotification()
    }

    // endregion
    // region 5. Runnable
    override fun run() {
        if (musicPlayer!!.isPlayable) {
            val position = musicPlayer!!.timePosition
            val intent = Intent(INTENT_FILTER_SERVICE)
            intent.putExtra(TYPE, INTENT_TYPE_POSITION)
            intent.putExtra(POSITION, position)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
        handler!!.postDelayed(this, 250)
    }

    // endregion
    // region 6. Notification
    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotification(musicTitle: String?, musicArtist: String?) {
        val broadcastIntentPrevious = Intent(this, NotificationReceiver::class.java)
        broadcastIntentPrevious.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PREVIOUS)
        val broadcastIntentNext = Intent(this, NotificationReceiver::class.java)
        broadcastIntentNext.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_NEXT)
        val broadcastIntentPlay = Intent(this, NotificationReceiver::class.java)
        if (musicPlayer!!.isPlaying) {
            broadcastIntentPlay.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PAUSE)
        } else {
            broadcastIntentPlay.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PLAY)
        }
        val artwork = BitmapFactory.decodeResource(resources, R.raw.ic_sound)
        notificationBuilder = NotificationCompat.Builder(this, App.CHANNEL_ID_1)
            .setSmallIcon(R.drawable.ic_baseline_music_24)
            .setContentTitle(musicTitle)
            .setContentText(musicArtist)
            .setShowWhen(false)
            .setColor(Color.RED)
            .setLargeIcon(artwork)
            .addAction(
                R.drawable.ic_baseline_previous_24, "previous",
                PendingIntent.getBroadcast(
                    this,
                    0,
                    broadcastIntentPrevious,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .addAction(
                if (musicPlayer!!.isPlaying) R.drawable.ic_baseline_pause_music else R.drawable.ic_baseline_play_music,
                "play",
                PendingIntent.getBroadcast(
                    this,
                    1,
                    broadcastIntentPlay,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .addAction(
                R.drawable.ic_baseline_next_24, "next",
                PendingIntent.getBroadcast(
                    this,
                    2,
                    broadcastIntentNext,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession!!.sessionToken)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOngoing(true)
        val notification = notificationBuilder!!.build()
        notificationManager!!.notify(NOTIFICATION_ID, notification)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun updateNotification() {
        val broadcastIntentPrevious = Intent(this, NotificationReceiver::class.java)
        broadcastIntentPrevious.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PREVIOUS)
        val broadcastIntentNext = Intent(this, NotificationReceiver::class.java)
        broadcastIntentNext.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_NEXT)
        val broadcastIntentPlay = Intent(this, NotificationReceiver::class.java)
        if (musicPlayer!!.isPlaying) {
            broadcastIntentPlay.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PAUSE)
        } else {
            broadcastIntentPlay.putExtra(NOTIFICATION_EXTRA, NOTIFICATION_PLAY)
        }
        notificationBuilder!!.clearActions()
        notificationBuilder!!.addAction(
            R.drawable.ic_baseline_previous_24, "previous",
            PendingIntent.getBroadcast(
                this,
                0,
                broadcastIntentPrevious,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        notificationBuilder!!.addAction(
            if (musicPlayer!!.isPlaying) R.drawable.ic_baseline_pause_music else R.drawable.ic_baseline_play_music,
            "play",
            PendingIntent.getBroadcast(
                this,
                1,
                broadcastIntentPlay,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        notificationBuilder!!.addAction(
            R.drawable.ic_baseline_next_24, "next",
            PendingIntent.getBroadcast(
                this,
                2,
                broadcastIntentNext,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        )
        val notification = notificationBuilder!!.build()
        notificationManager!!.notify(NOTIFICATION_ID, notification)
    }

    fun deleteNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    } // endregion

    companion object {
        const val INTENT_FILTER_SERVICE = "data_service"
        const val INTENT_TYPE_PREPARED = "type_prepared"
        const val INTENT_TYPE_PLAY = "type_play"
        const val INTENT_TYPE_PAUSE = "type_pause"
        const val INTENT_TYPE_STOP = "type_stop"
        const val INTENT_TYPE_POSITION = "type_position"
        const val TYPE = "type"
        const val IMAGE_RESOURCE = "img_resource"
        const val TITLE = "title"
        const val ARTIST = "artist"
        const val LENGTH = "length"
        const val POSITION = "position"
        const val PLAYLIST_ID = "playlist_id"
        const val AUDIO_SESSION_ID = "audio_session_id"
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_EXTRA = "notification_extra"
        const val NOTIFICATION_PREVIOUS = "previous"
        const val NOTIFICATION_NEXT = "next"
        const val NOTIFICATION_PLAY = "play"
        const val NOTIFICATION_PAUSE = "pause"
    }
}