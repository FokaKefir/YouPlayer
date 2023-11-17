package com.fokakefir.musicplayer.gui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.activity.MainActivity
import com.fokakefir.musicplayer.gui.recyclerview.MusicAdapter
import com.fokakefir.musicplayer.gui.recyclerview.MusicAdapter.OnMusicListener
import com.fokakefir.musicplayer.model.Music

class MusicsFragment(private val activity: MainActivity, @JvmField val playlistId: Int) : Fragment(),
    OnMusicListener {
    private lateinit var view: View
    private lateinit var recyclerView: RecyclerView
    private var adapter: MusicAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_musics, container, false)
        var cursor: Cursor? = null
        cursor = if (playlistId == MainActivity.DEFAULT_PLAYLIST_ID) {
            activity.allMusic
        } else {
            activity.getAllMusic(playlistId)
        }
        recyclerView = view.findViewById(R.id.recycler_view_musics)
        layoutManager = LinearLayoutManager(context)
        adapter = MusicAdapter(cursor, this, playlistId)
        recyclerView.setLayoutManager(layoutManager)
        recyclerView.setAdapter(adapter)
        return view
    }

    override fun onMusicClick(music: Music) {
        activity.playMusic(music, playlistId)
    }

    override fun onAddToQueueMusicClick(music: Music) {
        activity.addMusicToQueue(music)
    }

    override fun onAddMusicClick(music: Music) {
        activity.addChoosePlaylistFragment(music.id)
    }

    override fun onRemoveMusicClick(music: Music) {
        activity.deleteConnection(playlistId, music.id)
    }

    override fun onDeleteMusicClick(music: Music) {
        val dialogListener = DialogInterface.OnClickListener { dialogInterface, i ->
            if (i == DialogInterface.BUTTON_POSITIVE) {
                activity.deleteMusicFromStorage(music)
            }
        }
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Are you sure?")
            .setPositiveButton("Yes", dialogListener)
            .setNegativeButton("No", dialogListener)
            .show()
    }

    fun swapCursor(cursor: Cursor?) {
        adapter!!.swapCursor(cursor)
    }
}