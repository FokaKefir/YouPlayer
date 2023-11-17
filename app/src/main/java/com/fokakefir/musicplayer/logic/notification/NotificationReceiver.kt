package com.fokakefir.musicplayer.logic.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fokakefir.musicplayer.gui.activity.MainActivity
import com.fokakefir.musicplayer.logic.player.MusicPlayerService

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val args = intent.extras
        when (args!!.getString(MusicPlayerService.NOTIFICATION_EXTRA)) {
            MusicPlayerService.NOTIFICATION_PLAY -> playMusic(context)
            MusicPlayerService.NOTIFICATION_PAUSE -> pauseMusic(context)
            MusicPlayerService.NOTIFICATION_PREVIOUS -> previousMusic(context)
            MusicPlayerService.NOTIFICATION_NEXT -> nextMusic(context)
        }
    }

    fun playMusic(context: Context?) {
        val intent = Intent(INTENT_FILTER_NOTIFICATION_BROADCAST)
        intent.putExtra(MainActivity.TYPE, MainActivity.INTENT_TYPE_PLAY)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    fun pauseMusic(context: Context?) {
        val intent = Intent(INTENT_FILTER_NOTIFICATION_BROADCAST)
        intent.putExtra(MainActivity.TYPE, MainActivity.INTENT_TYPE_PAUSE)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    fun previousMusic(context: Context?) {
        val intent = Intent(INTENT_FILTER_NOTIFICATION_BROADCAST)
        intent.putExtra(MainActivity.TYPE, MainActivity.INTENT_TYPE_PREVIOUS)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    fun nextMusic(context: Context?) {
        val intent = Intent(INTENT_FILTER_NOTIFICATION_BROADCAST)
        intent.putExtra(MainActivity.TYPE, MainActivity.INTENT_TYPE_NEXT)
        LocalBroadcastManager.getInstance(context!!).sendBroadcast(intent)
    }

    companion object {
        const val INTENT_FILTER_NOTIFICATION_BROADCAST = "data_notification_broadcast"
    }
}