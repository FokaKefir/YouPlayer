package com.fokakefir.musicplayer.gui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.os.Environment
import android.util.SparseArray
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.fragment.ChoosePlaylistFragment
import com.fokakefir.musicplayer.gui.fragment.MusicsFragment
import com.fokakefir.musicplayer.gui.fragment.PlaylistsFragment
import com.fokakefir.musicplayer.gui.fragment.SearchFragment
import com.fokakefir.musicplayer.logic.background.RequestDownloadMusicStream
import com.fokakefir.musicplayer.logic.background.RequestDownloadMusicStreamResponse
import com.fokakefir.musicplayer.logic.background.RequestDownloadThumbnailStream
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract.ConnectEntry
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract.MusicEntry
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract.PlaylistEntry
import com.fokakefir.musicplayer.logic.database.MusicPlayerDBHelper
import com.fokakefir.musicplayer.logic.network.YoutubeAPI
import com.fokakefir.musicplayer.logic.player.MusicPlayerService
import com.fokakefir.musicplayer.logic.ytextraction.VideoMeta
import com.fokakefir.musicplayer.logic.ytextraction.YouTubeExtractor
import com.fokakefir.musicplayer.logic.ytextraction.YtFile
import com.fokakefir.musicplayer.model.Music
import com.gauravk.audiovisualizer.base.BaseVisualizer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    SlidingUpPanelLayout.PanelSlideListener, View.OnClickListener,
    RequestDownloadMusicStreamResponse, OnSeekBarChangeListener {
    // region 1. Decl and Init
    private var serviceIntent: Intent? = null
    private var database: SQLiteDatabase? = null
    private var searchFragment: SearchFragment? = null
    private var playlistsFragment: PlaylistsFragment? = null
    private var musicsFragment: MusicsFragment? = null
    private var choosePlaylistFragment: ChoosePlaylistFragment? = null
    private var bottomNav: BottomNavigationView? = null
    private var layout: SlidingUpPanelLayout? = null
    private var txtMusicTitleDown: TextView? = null
    private var txtMusicArtistDown: TextView? = null
    private var btnPlayDown: ImageButton? = null
    private var visualizer: BaseVisualizer? = null
    private var txtMusicTitleUp: TextView? = null
    private var txtMusicArtistUp: TextView? = null
    private var txtCurrentTime: TextView? = null
    private var txtFinalTime: TextView? = null
    private var seekBar: SeekBar? = null
    private var btnPlayUp: CircleImageView? = null
    private var btnPrevious: ImageButton? = null
    private var btnNext: ImageButton? = null
    private var btnShuffle: ImageButton? = null
    private var btnRepeat: ImageButton? = null
    private var slidingSeekBar = false
    private var isPlaying = false
    private var isPlayable = false
    private var isShuffle = false
    private var isRepeat = false
    private var currentPlaylistId = 0
    private var currentAudioSessionId = 0
    private var downloadingMusicList: MutableList<String>? = null

    // endregion
    // region 2. Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbHelper = MusicPlayerDBHelper(this)

        database = dbHelper.writableDatabase
        searchFragment = SearchFragment(this)
        playlistsFragment = PlaylistsFragment(this)
        musicsFragment = null
        choosePlaylistFragment = null

        bottomNav = findViewById(R.id.bottom_navigation)
        layout = findViewById(R.id.sliding_up_panel)
        txtMusicTitleDown = findViewById(R.id.txt_music_title_down)
        txtMusicArtistDown = findViewById(R.id.txt_music_artist_down)
        btnPlayDown = findViewById(R.id.btn_play_music_down)
        visualizer = findViewById(R.id.circle_line_visualizer)
        txtMusicTitleUp = findViewById(R.id.txt_music_title_up)
        txtMusicArtistUp = findViewById(R.id.txt_music_artist_up)
        txtCurrentTime = findViewById(R.id.txt_current_time)
        txtFinalTime = findViewById(R.id.txt_final_time)
        seekBar = findViewById(R.id.seek_bar)
        btnPlayUp = findViewById(R.id.btn_play_music_up)
        btnPrevious = findViewById(R.id.btn_previous_music)
        btnNext = findViewById(R.id.btn_next_music)
        btnShuffle = findViewById(R.id.btn_shuffle_music)
        btnRepeat = findViewById(R.id.btn_repeat_music)

        txtMusicTitleDown!!.setSelected(true)
        txtMusicTitleUp!!.setSelected(true)
        bottomNav!!.setOnNavigationItemSelectedListener(this)

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, searchFragment!!)
            .commit()
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, playlistsFragment!!)
            .hide(
                playlistsFragment!!
            ).commit()

        btnPlayDown!!.setOnClickListener(this)
        btnPlayUp!!.setOnClickListener(this)
        btnPrevious!!.setOnClickListener(this)
        btnNext!!.setOnClickListener(this)
        btnShuffle!!.setOnClickListener(this)
        btnRepeat!!.setOnClickListener(this)
        layout!!.addPanelSlideListener(this)
        seekBar!!.setOnSeekBarChangeListener(this)

        slidingSeekBar = false
        serviceIntent = Intent(this@MainActivity, MusicPlayerService::class.java)
        startForegroundService(serviceIntent)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            receiver, IntentFilter(
                MusicPlayerService.INTENT_FILTER_SERVICE
            )
        )
        isPlaying = false
        isPlayable = false
        isShuffle = false
        isRepeat = false
        currentPlaylistId = 0
        currentAudioSessionId = -1
        downloadingMusicList = ArrayList()
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
        )
        if (!hasPermissions(this, *permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_CODE)
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        if (visualizer != null) {
            visualizer!!.release()
        }
        stopService(serviceIntent)
    }

    override fun onBackPressed() {
        if (bottomNav!!.selectedItemId == R.id.nav_playlists) {
            if (choosePlaylistFragment != null) {
                musicsFragment = null
                super.onBackPressed()
            } else if (musicsFragment != null) {
                musicsFragment = null
                super.onBackPressed()
            }
        }
    }

    // endregion
    // region 3. Fragments
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.isChecked) {
            return true
        } else {
            when (item.itemId) {
                R.id.nav_search -> {
                    supportFragmentManager.beginTransaction().hide(playlistsFragment!!).commit()
                    supportFragmentManager.beginTransaction().show(searchFragment!!).commit()
                    if (musicsFragment != null) {
                        supportFragmentManager.beginTransaction().hide(musicsFragment!!).commit()
                    }
                    if (choosePlaylistFragment != null) {
                        closeChoosePlaylistFragment()
                    }
                    return true
                }

                R.id.nav_playlists -> {
                    supportFragmentManager.beginTransaction().hide(searchFragment!!).commit()
                    supportFragmentManager.beginTransaction().show(playlistsFragment!!).commit()
                    if (musicsFragment != null) {
                        supportFragmentManager.beginTransaction().show(musicsFragment!!).commit()
                    }
                    return true
                }
            }
        }
        return false
    }

    fun addMusicsFragment(playlistId: Int) {
        musicsFragment = MusicsFragment(this, playlistId)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, musicsFragment!!).addToBackStack(null).commit()
    }

    fun addChoosePlaylistFragment(musicId: Int) {
        choosePlaylistFragment = ChoosePlaylistFragment(this, musicId)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, choosePlaylistFragment!!).addToBackStack(null).commit()
    }

    fun closeChoosePlaylistFragment() {
        supportFragmentManager.popBackStack()
    }

    // endregion
    // region 4. Button listener
    override fun onClick(view: View) {
        if (view.id == R.id.btn_play_music_down || view.id == R.id.btn_play_music_up) {
            if (isPlayable) {
                val intent = Intent(INTENT_FILTER_ACTIVITY)
                if (isPlaying) {
                    intent.putExtra(TYPE, INTENT_TYPE_PAUSE)
                } else {
                    intent.putExtra(TYPE, INTENT_TYPE_PLAY)
                }
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        } else if (view.id == R.id.btn_previous_music) {
            if (isPlayable) {
                val intent = Intent(INTENT_FILTER_ACTIVITY)
                intent.putExtra(TYPE, INTENT_TYPE_PREVIOUS)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        } else if (view.id == R.id.btn_next_music) {
            if (isPlayable) {
                val intent = Intent(INTENT_FILTER_ACTIVITY)
                intent.putExtra(TYPE, INTENT_TYPE_NEXT)
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }
        } else if (view.id == R.id.btn_shuffle_music) {
            val intent = Intent(INTENT_FILTER_ACTIVITY)
            intent.putExtra(TYPE, INTENT_TYPE_SHUFFLE)
            intent.putExtra(SHUFFLE, !isShuffle)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            if (isShuffle) btnShuffle!!.setImageResource(R.drawable.ic_shuffle_off) else btnShuffle!!.setImageResource(
                R.drawable.ic_shuffle_on
            )
            isShuffle = !isShuffle
        } else if (view.id == R.id.btn_repeat_music) {
            val intent = Intent(INTENT_FILTER_ACTIVITY)
            intent.putExtra(TYPE, INTENT_TYPE_REPEAT)
            intent.putExtra(REPEAT, !isRepeat)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            if (isRepeat) btnRepeat!!.setImageResource(R.drawable.ic_repeat_off) else btnRepeat!!.setImageResource(
                R.drawable.ic_repeat_on
            )
            isRepeat = !isRepeat
        }
    }

    // endregion
    // region 5. MusicPlayer
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            when (bundle!!.getString(MusicPlayerService.TYPE)) {
                MusicPlayerService.INTENT_TYPE_PREPARED -> {
                    setBtnPlayImage(bundle.getInt(MusicPlayerService.IMAGE_RESOURCE))
                    setMusicTexts(
                        bundle.getString(MusicPlayerService.TITLE),
                        bundle.getString(MusicPlayerService.ARTIST)
                    )
                    setMusicSeekBar(bundle.getInt(MusicPlayerService.LENGTH))
                    isPlaying = true
                    isPlayable = true
                    currentPlaylistId = bundle.getInt(MusicPlayerService.PLAYLIST_ID)
                    val audioSessionId = bundle.getInt(MusicPlayerService.AUDIO_SESSION_ID)
                    if (audioSessionId != -1 && audioSessionId != currentAudioSessionId) {
                        visualizer!!.setAudioSessionId(bundle.getInt(MusicPlayerService.AUDIO_SESSION_ID))
                        currentAudioSessionId = audioSessionId
                    }
                    seekBar!!.progress = 0
                }

                MusicPlayerService.INTENT_TYPE_PLAY -> {
                    setBtnPlayImage(bundle.getInt(MusicPlayerService.IMAGE_RESOURCE))
                    isPlaying = true
                }

                MusicPlayerService.INTENT_TYPE_PAUSE -> {
                    setBtnPlayImage(bundle.getInt(MusicPlayerService.IMAGE_RESOURCE))
                    isPlaying = false
                }

                MusicPlayerService.INTENT_TYPE_STOP -> {
                    setBtnPlayImage(bundle.getInt(MusicPlayerService.IMAGE_RESOURCE))
                    setMusicTexts("Title", "Artist")
                    setMusicSeekBar(0)
                    isPlaying = false
                    isPlayable = false
                }

                MusicPlayerService.INTENT_TYPE_POSITION -> setMusicSeekBarPosition(
                    bundle.getInt(
                        MusicPlayerService.POSITION
                    )
                )
            }
        }
    }

    fun playMusic(music: Music, playlistId: Int) {
        val strUri = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString() +
                "/YoutubeMusics/" + music.videoId + AUDIO_FORMAT
        val intent = Intent(INTENT_FILTER_ACTIVITY)
        intent.putExtra(TYPE, INTENT_TYPE_PLAY_URI)
        intent.putExtra(CURRENT_MUSIC, music)
        intent.putExtra(MUSICS, getMusics(playlistId))
        intent.putExtra(URI, strUri)
        intent.putExtra(PLAYLIST_ID, playlistId)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    @SuppressLint("SetTextI18n")
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (isPlayable && fromUser) {
            val minute = progress / 60
            val second = progress % 60
            val strMinute = minute.toString()
            val strSecond: String
            strSecond = if (second < 10) "0$second" else second.toString()
            txtCurrentTime!!.text = "$strMinute:$strSecond"
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        slidingSeekBar = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        slidingSeekBar = false
        if (isPlayable) {
            val intent = Intent(INTENT_FILTER_ACTIVITY)
            intent.putExtra(TYPE, INTENT_TYPE_PROGRESS)
            intent.putExtra(PROGRESS, seekBar.progress)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    fun addMusicToMusicPlayer(playlistId: Int, musicId: Int) {
        val newMusic = getMusicById(playlistId, musicId)
        if (newMusic != null) {
            val intent = Intent(INTENT_FILTER_ACTIVITY)
            intent.putExtra(TYPE, INTENT_TYPE_INSERT_NEW_MUSIC)
            intent.putExtra(NEW_MUSIC, newMusic)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    fun addMusicToQueue(music: Music?) {
        val intent = Intent(INTENT_FILTER_ACTIVITY)
        intent.putExtra(TYPE, INTENT_TYPE_QUEUE_MUSIC)
        intent.putExtra(NEW_MUSIC, music)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    // endregion
    // region 6. SlidingUpPanel
    override fun onPanelSlide(panel: View, slideOffset: Float) {
        findViewById<View>(R.id.layout_down).alpha = 1 - slideOffset
        findViewById<View>(R.id.layout_up).alpha = slideOffset
    }

    override fun onPanelStateChanged(
        panel: View,
        previousState: PanelState,
        newState: PanelState
    ) {
    }

    // endregion
    // region 7. Download and delete music
    fun downloadMusic(url: String?, videoId: String, videoArtist: String?) {
        if (!isMusicAlreadyDownloaded(videoId) && !isMusicActuallyDownloading(videoId)) {
            object : YouTubeExtractor(this) {
                override fun onExtractionComplete(
                    ytFiles: SparseArray<YtFile>?,
                    videoMeta: VideoMeta?
                ) {
                    if (ytFiles != null) {
                        try {
                            val downloadUrl = ytFiles[YoutubeAPI.YOUTUBE_ITAG_AUDIO_128K].url
                            if (downloadUrl != null) {
                                val requestDownloadMusicStream =
                                    RequestDownloadMusicStream(this@MainActivity, this@MainActivity)
                                requestDownloadMusicStream.execute(
                                    downloadUrl,
                                    videoId,
                                    videoMeta!!.title,
                                    videoArtist
                                )
                                downloadingMusicList!!.add(videoId)
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }.extract(url, true, true)
        } else {
            Toast.makeText(
                this,
                "Music is already downloaded or actually downloading",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMusicDownloaded(videoId: String, title: String, artist: String, length: Int) {
        insertMusic(videoId, title, artist, length)
        downloadingMusicList!!.remove(videoId)
    }

    fun downloadThumbnail(url: String?, name: String?) {
        RequestDownloadThumbnailStream(this).execute(url, name)
    }

    fun deleteMusicFromStorage(music: Music) {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/YoutubeMusics"
        )
        val file = File(dir, music.videoId + AUDIO_FORMAT)
        if (file.exists() && file.canWrite()) {
            val deleted = file.delete()
            if (deleted) deleteMusic(music)
        }
    }

    // endregion
    // region 8. Database
    fun insertMusic(videoId: String?, title: String?, artist: String?, length: Int) {
        val cvMusic = ContentValues()
        cvMusic.put(MusicEntry.COLUMN_VIDEO_ID, videoId)
        cvMusic.put(MusicEntry.COLUMN_TITLE, title)
        cvMusic.put(MusicEntry.COLUMN_ARTIST, artist)
        cvMusic.put(MusicEntry.COLUMN_LENGTH, length)
        val id = database!!.insert(MusicEntry.TABLE_NAME, null, cvMusic)
        val cvConnect = ContentValues()
        cvConnect.put(ConnectEntry.COLUMN_PLAYLIST_ID, DEFAULT_PLAYLIST_ID)
        cvConnect.put(ConnectEntry.COLUMN_MUSIC_ID, id)
        database!!.insert(ConnectEntry.TABLE_NAME, null, cvConnect)
        playlistsFragment!!.swapCursor(allPlaylists)
        if (musicsFragment != null) musicsFragment!!.swapCursor(getAllMusic(musicsFragment!!.playlistId))
        if (currentPlaylistId == DEFAULT_PLAYLIST_ID) {
            addMusicToMusicPlayer(DEFAULT_PLAYLIST_ID, id.toInt())
        }
        Toast.makeText(this, "Downloaded", Toast.LENGTH_SHORT).show()
    }

    fun insertPlaylist(name: String?, color: String?) {
        val cv = ContentValues()
        cv.put(PlaylistEntry.COLUMN_NAME, name)
        cv.put(PlaylistEntry.COLUMN_COLOR, color)
        database!!.insert(PlaylistEntry.TABLE_NAME, null, cv)
        playlistsFragment!!.swapCursor(allPlaylists)
    }

    fun insertConnection(playlistId: Int, musicId: Int) {
        val cv = ContentValues()
        cv.put(ConnectEntry.COLUMN_PLAYLIST_ID, playlistId)
        cv.put(ConnectEntry.COLUMN_MUSIC_ID, musicId)
        database!!.insert(ConnectEntry.TABLE_NAME, null, cv)
        playlistsFragment!!.swapCursor(allPlaylists)
        if (musicsFragment != null) musicsFragment!!.swapCursor(getAllMusic(musicsFragment!!.playlistId))
        closeChoosePlaylistFragment()
        if (currentPlaylistId == playlistId) {
            addMusicToMusicPlayer(playlistId, musicId)
        }
        Toast.makeText(this, "Music added to playlist", Toast.LENGTH_SHORT).show()
    }

    fun updatePlaylist(playlistId: Int, newName: String?, newColor: String?) {
        val cv = ContentValues()
        cv.put(PlaylistEntry.COLUMN_NAME, newName)
        cv.put(PlaylistEntry.COLUMN_COLOR, newColor)
        database!!.update(
            PlaylistEntry.TABLE_NAME,
            cv,
            PlaylistEntry._ID + "=?",
            arrayOf(playlistId.toString())
        )
        playlistsFragment!!.swapCursor(allPlaylists)
    }

    fun deleteMusic(music: Music) {
        database!!.delete(
            MusicEntry.TABLE_NAME,
            MusicEntry._ID + "=?",
            arrayOf(music.id.toString())
        )
        database!!.delete(
            ConnectEntry.TABLE_NAME,
            ConnectEntry.COLUMN_MUSIC_ID + "=?",
            arrayOf(music.id.toString())
        )
        playlistsFragment!!.swapCursor(allPlaylists)
        if (musicsFragment != null) musicsFragment!!.swapCursor(getAllMusic(musicsFragment!!.playlistId))
        Toast.makeText(this, "Music deleted", Toast.LENGTH_SHORT).show()
    }

    fun deletePlaylist(playlistId: Int) {
        database!!.delete(
            PlaylistEntry.TABLE_NAME,
            PlaylistEntry._ID + "=?",
            arrayOf(playlistId.toString())
        )
        database!!.delete(
            ConnectEntry.TABLE_NAME,
            ConnectEntry.COLUMN_PLAYLIST_ID + "=?",
            arrayOf(playlistId.toString())
        )
        playlistsFragment!!.swapCursor(allPlaylists)
        Toast.makeText(this, "Playlist deleted", Toast.LENGTH_SHORT).show()
    }

    fun deleteConnection(playlistId: Int, musicId: Int) {
        database!!.delete(
            ConnectEntry.TABLE_NAME,
            ConnectEntry.COLUMN_PLAYLIST_ID + "=? AND " + ConnectEntry.COLUMN_MUSIC_ID + "=?",
            arrayOf(playlistId.toString(), musicId.toString())
        )
        playlistsFragment!!.swapCursor(allPlaylists)
        if (musicsFragment != null) musicsFragment!!.swapCursor(getAllMusic(musicsFragment!!.playlistId))
        Toast.makeText(this, "Music removed from playlist", Toast.LENGTH_SHORT).show()
    }

    val allPlaylists: Cursor
        // endregion
        get() = database!!.rawQuery(
            "SELECT " + PlaylistEntry._ID + ", " + PlaylistEntry.COLUMN_NAME + ", " + PlaylistEntry.COLUMN_COLOR + ", " +
                    "(SELECT COUNT(" + ConnectEntry.COLUMN_MUSIC_ID + ") FROM " + ConnectEntry.TABLE_NAME + ", " + MusicEntry.TABLE_NAME +
                    " WHERE " + PlaylistEntry.TABLE_NAME + "." + PlaylistEntry._ID + "=" + ConnectEntry.COLUMN_PLAYLIST_ID +
                    " AND " + ConnectEntry.COLUMN_MUSIC_ID + "=" + MusicEntry.TABLE_NAME + "." + MusicEntry._ID +
                    ") AS " + PlaylistEntry.COLUMN_MUSICS +
                    " FROM " + PlaylistEntry.TABLE_NAME +
                    " ORDER BY " + PlaylistEntry.COLUMN_TIMESTAMP + " ASC;",
            null
        )

    fun getAllChoosePlaylists(musicId: Int): Cursor {
        val SQL_SELECT_ALL_CHOOSE_PLAYLISTS =
            "SELECT DISTINCT " + PlaylistEntry._ID + ", " + PlaylistEntry.COLUMN_NAME + ", " + PlaylistEntry.COLUMN_COLOR + ", " +
                    "(SELECT COUNT(" + ConnectEntry.COLUMN_MUSIC_ID + ") FROM " + ConnectEntry.TABLE_NAME + ", " + MusicEntry.TABLE_NAME +
                    " WHERE " + PlaylistEntry.TABLE_NAME + "." + PlaylistEntry._ID + "=" + ConnectEntry.COLUMN_PLAYLIST_ID +
                    " AND " + ConnectEntry.COLUMN_MUSIC_ID + "=" + MusicEntry.TABLE_NAME + "." + MusicEntry._ID +
                    ") AS " + PlaylistEntry.COLUMN_MUSICS +
                    " FROM " + PlaylistEntry.TABLE_NAME +
                    " WHERE " + PlaylistEntry._ID + " NOT IN " +
                    "(SELECT " + ConnectEntry.COLUMN_PLAYLIST_ID +
                    " FROM " + ConnectEntry.TABLE_NAME +
                    " WHERE " + ConnectEntry.COLUMN_MUSIC_ID + "=?)" +
                    " ORDER BY " + PlaylistEntry.COLUMN_TIMESTAMP + " ASC;"
        return database!!.rawQuery(
            SQL_SELECT_ALL_CHOOSE_PLAYLISTS, arrayOf(musicId.toString())
        )
    }

    val allMusic: Cursor
        get() = database!!.query(
            MusicEntry.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            MusicEntry.COLUMN_TIMESTAMP + " ASC"
        )

    fun getAllMusic(playlistId: Int): Cursor {
        if (playlistId == DEFAULT_PLAYLIST_ID) return allMusic
        val SQL_SELECT_ALL_MUSIC = "SELECT " + MusicEntry.TABLE_NAME + ".*" + " FROM " +
                MusicEntry.TABLE_NAME + ", " + PlaylistEntry.TABLE_NAME + ", " + ConnectEntry.TABLE_NAME +
                " WHERE " + MusicEntry.TABLE_NAME + "." + MusicEntry._ID + "=" + ConnectEntry.COLUMN_MUSIC_ID +
                " AND " + PlaylistEntry.TABLE_NAME + "." + PlaylistEntry._ID + "=" + ConnectEntry.COLUMN_PLAYLIST_ID +
                " AND " + PlaylistEntry.TABLE_NAME + "." + PlaylistEntry._ID + "=?" +
                " ORDER BY " + ConnectEntry.COLUMN_TIMESTAMP + " ASC;"
        return database!!.rawQuery(
            SQL_SELECT_ALL_MUSIC, arrayOf(playlistId.toString())
        )
    }

    fun getMusics(playlistId: Int): ArrayList<Music> {
        val musics = ArrayList<Music>()
        val cursor = getAllMusic(playlistId)
        if (cursor.moveToFirst()) {
            do {
                val currentMusic = Music(
                    cursor.getInt(cursor.getColumnIndex(MusicEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(MusicEntry.COLUMN_VIDEO_ID)),
                    cursor.getString(cursor.getColumnIndex(MusicEntry.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(MusicEntry.COLUMN_ARTIST)),
                    cursor.getInt(cursor.getColumnIndex(MusicEntry.COLUMN_LENGTH))
                )
                musics.add(currentMusic)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return musics
    }

    fun getMusicById(playlistId: Int, musicId: Int): Music? {
        val musics = getMusics(playlistId)
        for (music in musics) {
            if (music.id == musicId) return music
        }
        return null
    }

    fun isMusicAlreadyDownloaded(videoId: String): Boolean {
        val cursor = database!!.rawQuery(
            "SELECT * FROM " + MusicEntry.TABLE_NAME + " WHERE " + MusicEntry.COLUMN_VIDEO_ID + "=?",
            arrayOf(videoId)
        )
        return cursor.count > 0
    }

    fun isMusicActuallyDownloading(videoId: String): Boolean {
        return if (downloadingMusicList!!.isEmpty()) false else downloadingMusicList!!.contains(
            videoId
        )
    }

    fun setBtnPlayImage(resId: Int) {
        btnPlayDown!!.setImageResource(resId)
        btnPlayUp!!.setImageResource(resId)
    }

    fun setMusicTexts(title: String?, artist: String?) {
        txtMusicTitleDown!!.text = title
        txtMusicArtistDown!!.text = artist
        txtMusicTitleUp!!.text = title
        txtMusicArtistUp!!.text = artist
    }

    @SuppressLint("SetTextI18n")
    fun setMusicSeekBar(length: Int) {
        seekBar!!.max = length
        txtCurrentTime!!.text = "0:00"
        val minute = length / 60
        val second = length % 60
        val strMinute = minute.toString()
        val strSecond: String
        strSecond = if (second < 10) "0$second" else second.toString()
        txtFinalTime!!.text = "$strMinute:$strSecond"
    }

    @SuppressLint("SetTextI18n")
    fun setMusicSeekBarPosition(position: Int) {
        if (isPlayable && !slidingSeekBar) {
            seekBar!!.progress = position
            val minute = position / 60
            val second = position % 60
            val strMinute = minute.toString()
            val strSecond: String
            strSecond = if (second < 10) "0$second" else second.toString()
            txtCurrentTime!!.text = "$strMinute:$strSecond"
        }
    } // endregion

    companion object {
        private const val PERMISSIONS_CODE = 69
        const val DEFAULT_PLAYLIST_ID = 1
        const val AUDIO_FORMAT = ".m4a"
        const val INTENT_FILTER_ACTIVITY = "data_activity"
        const val INTENT_TYPE_PLAY_URI = "play_uri"
        const val INTENT_TYPE_PLAY = "play"
        const val INTENT_TYPE_PAUSE = "pause"
        const val INTENT_TYPE_NEXT = "next"
        const val INTENT_TYPE_PREVIOUS = "previous"
        const val INTENT_TYPE_PROGRESS = "progress"
        const val INTENT_TYPE_INSERT_NEW_MUSIC = "insert_new_music"
        const val INTENT_TYPE_SHUFFLE = "shuffle"
        const val INTENT_TYPE_REPEAT = "repeat"
        const val INTENT_TYPE_QUEUE_MUSIC = "queue"
        const val TYPE = "type"
        const val CURRENT_MUSIC = "current_music"
        const val MUSICS = "musics"
        const val URI = "uri"
        const val PROGRESS = "progress"
        const val PLAYLIST_ID = "playlist_id"
        const val NEW_MUSIC = "new_music"
        const val SHUFFLE = "shuffle"
        const val REPEAT = "repeat"
        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission!!
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }
    }
}