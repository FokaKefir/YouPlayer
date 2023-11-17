package com.fokakefir.musicplayer.logic.player

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Environment
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.activity.MainActivity
import com.fokakefir.musicplayer.model.Music
import java.io.IOException
import java.util.Collections
import java.util.concurrent.ThreadLocalRandom

class MusicPlayer(
    private val service: MusicPlayerService, private val listener: MusicPlayerListener
) : OnCompletionListener {
    // region 1. Decl and Init
    private var mediaPlayer: MediaPlayer? = null
    @JvmField
    var currentMusic: Music? = null
    private var musics: ArrayList<Music?>? = null
    private var shuffleMusics: ArrayList<Music?>? = null
    private val queueMusics: ArrayList<Music>?
    private var shuffle = false
    var isRepeat = false
    private var playlistId = 0

    // endregion
    // region 2. Constructor
    init {
        queueMusics = ArrayList()
    }

    // endregion
    // region 3. MediaPlayer
    fun playMusicUri(uri: Uri?) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mediaPlayer!!.setOnCompletionListener(this)
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
        }
        try {
            mediaPlayer!!.setDataSource(service, uri!!)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            listener.onPreparedMusic(
                R.drawable.ic_baseline_pause_music,
                currentMusic!!.title,
                currentMusic!!.artist,
                currentMusic!!.length,
                playlistId,
                mediaPlayer!!.audioSessionId
            )
        } catch (e: IOException) {
            e.printStackTrace()
            mediaPlayer = null
        }
    }

    fun playMusicUri(uri: Uri?, music: Music) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mediaPlayer!!.setOnCompletionListener(this)
        } else {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
        }
        try {
            mediaPlayer!!.setDataSource(service, uri!!)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            listener.onPreparedMusic(
                R.drawable.ic_baseline_pause_music,
                music.title,
                music.artist,
                music.length,
                playlistId,
                mediaPlayer!!.audioSessionId
            )
        } catch (e: IOException) {
            e.printStackTrace()
            mediaPlayer = null
        }
    }

    fun playMusic() {
        if (mediaPlayer != null) {
            mediaPlayer!!.start()
            listener.onPlayMusic(R.drawable.ic_baseline_pause_music)
        }
    }

    fun pauseMusic() {
        if (mediaPlayer != null) {
            mediaPlayer!!.pause()
            listener.onPauseMusic(R.drawable.ic_baseline_play_music)
        }
    }

    fun previousMusic() {
        if (currentMusic != null) {
            val position = currentMusicPosition - 1
            if (position >= 0 || isRepeat && position == -1) {
                currentMusic = getMusicFromPosition((position + musics!!.size) % musics!!.size)
                val uri = Uri.parse(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() +
                            "/YoutubeMusics/" + currentMusic!!.videoId + MainActivity.AUDIO_FORMAT
                )
                this.playMusicUri(uri)
            }
        }
    }

    fun nextMusic() {
        if (queueMusics != null && !queueMusics.isEmpty()) {
            val queueMusic = queueMusics[0]
            val uri = Uri.parse(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() +
                        "/YoutubeMusics/" + queueMusic.videoId + MainActivity.AUDIO_FORMAT
            )
            this.playMusicUri(uri, queueMusic)
            queueMusics.removeAt(0)
        } else if (currentMusic != null) {
            val position = currentMusicPosition + 1
            if (position < musics!!.size || isRepeat && position == musics!!.size) {
                currentMusic = getMusicFromPosition(position % musics!!.size)
                val uri = Uri.parse(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .toString() +
                            "/YoutubeMusics/" + currentMusic!!.videoId + MainActivity.AUDIO_FORMAT
                )
                this.playMusicUri(uri)
            }
        }
    }

    fun stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mediaPlayer = null
            listener.onStopMediaPlayer(R.drawable.ic_baseline_play_music)
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        if (currentMusicPosition < musics!!.size - 1) {
            nextMusic()
        } else {
            if (isRepeat || queueMusics != null && !queueMusics.isEmpty()) {
                nextMusic()
            } else {
                stopMediaPlayer()
            }
        }
    }

    // endregion
    // region 4. Getters and Setters
    private fun getMusicFromPosition(index: Int): Music? {
        return if (shuffle) {
            shuffleMusics!![index]
        } else {
            musics!![index]
        }
    }

    private val currentMusicPosition: Int
        private get() {
            if (!shuffle) {
                for (ind in musics!!.indices) {
                    if (currentMusic!!.id == musics!![ind]!!.id) return ind
                }
            } else {
                for (ind in musics!!.indices) {
                    if (currentMusic!!.id == shuffleMusics!![ind]!!.id) return ind
                }
            }
            return -1
        }
    val timePosition: Int
        get() = if (mediaPlayer != null) {
            mediaPlayer!!.currentPosition / 1000
        } else 0

    fun getMusics(): ArrayList<Music?>? {
        return musics
    }

    fun isShuffle(): Boolean {
        return shuffle
    }

    val isPlayable: Boolean
        get() = mediaPlayer != null
    val isPlaying: Boolean
        get() = mediaPlayer!!.isPlaying

    fun setMusics(musics: ArrayList<Music?>?) {
        this.musics = musics
        shuffleMusics = ArrayList(musics)
        Collections.shuffle(shuffleMusics)
    }

    fun setShuffle(shuffle: Boolean) {
        this.shuffle = shuffle
        if (this.shuffle && shuffleMusics != null) Collections.shuffle(shuffleMusics)
    }

    fun setProgress(progress: Int) {
        if (mediaPlayer != null) {
            mediaPlayer!!.seekTo((progress * 1000).toLong(), MediaPlayer.SEEK_CLOSEST)
        }
    }

    fun setPlaylistId(playlistId: Int) {
        this.playlistId = playlistId
    }

    fun insertNewMusic(newMusic: Music?) {
        musics!!.add(newMusic)
        val index = ThreadLocalRandom.current().nextInt(0, shuffleMusics!!.size + 1)
        shuffleMusics!!.add(index, newMusic)
    }

    fun addQueueMusic(queueMusic: Music) {
        queueMusics!!.add(queueMusic)
    }

    // endregion
    // region 5. Listener
    interface MusicPlayerListener {
        fun onPreparedMusic(
            imgResource: Int,
            title: String?,
            artist: String?,
            length: Int,
            playlistId: Int,
            audioSessionId: Int
        )

        fun onPlayMusic(imgResource: Int)
        fun onPauseMusic(imgResource: Int)
        fun onStopMediaPlayer(imgResource: Int)
    } // endregion
}