package com.fokakefir.musicplayer.gui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.activity.MainActivity
import com.fokakefir.musicplayer.gui.dialog.PlaylistDialog
import com.fokakefir.musicplayer.gui.dialog.PlaylistDialog.OnPlaylistDialogListener
import com.fokakefir.musicplayer.gui.recyclerview.PlaylistAdapter
import com.fokakefir.musicplayer.gui.recyclerview.PlaylistAdapter.OnPlaylistListener
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class PlaylistsFragment(private val activity: MainActivity) : Fragment(), OnPlaylistListener,
    View.OnClickListener, OnPlaylistDialogListener {

    private lateinit var view: View
    private lateinit var recyclerView: RecyclerView
    private var adapter: PlaylistAdapter? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var fabAddPlaylist: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_playlists, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_playlists)
        layoutManager = LinearLayoutManager(context)
        adapter = PlaylistAdapter(activity.allPlaylists, this, context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        fabAddPlaylist = view.findViewById(R.id.fab_add_playlist)
        fabAddPlaylist.setOnClickListener(this)
        return view
    }

    override fun onPlaylistClick(playlistId: Int) {
        activity.addMusicsFragment(playlistId)
    }

    override fun onEditPlaylistClick(playlistId: Int, name: String, color: String) {
        val dialog = PlaylistDialog(playlistId, name, color, this)
        dialog.show(activity.supportFragmentManager, "playlist dialog")
    }

    override fun onDeletePlaylistClick(playlistId: Int) {
        val dialogListener = DialogInterface.OnClickListener { dialogInterface, i ->
            if (i == DialogInterface.BUTTON_POSITIVE) {
                activity.deletePlaylist(playlistId)
            }
        }
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("Are you sure?")
            .setPositiveButton("Yes", dialogListener)
            .setNegativeButton("No", dialogListener)
            .show()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.fab_add_playlist) {
            val dialog = PlaylistDialog(this)
            dialog.show(activity.supportFragmentManager, "playlist dialog")
        }
    }

    fun swapCursor(cursor: Cursor?) {
        adapter!!.swapCursor(cursor)
    }

    override fun onPlaylistCreate(name: String?, color: String?) {
        if (!name!!.isEmpty()) activity.insertPlaylist(name, color) else Toast.makeText(
            activity,
            "Name can't be empty",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onPlaylistEdit(playlistId: Int, name: String?, color: String?) {
        if (!name!!.isEmpty()) activity.updatePlaylist(playlistId, name, color) else Toast.makeText(
            activity, "Name can't be empty", Toast.LENGTH_SHORT
        ).show()
    }

    fun setChooseMode(condition: Boolean) {
        if (condition) {
            fabAddPlaylist!!.visibility = View.GONE
            adapter!!.setOptions(false)
        } else {
            fabAddPlaylist!!.visibility = View.VISIBLE
            adapter!!.setOptions(true)
        }
        adapter!!.notifyDataSetChanged()
    }
}