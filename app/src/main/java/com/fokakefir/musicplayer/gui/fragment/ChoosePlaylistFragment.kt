package com.fokakefir.musicplayer.gui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fokakefir.musicplayer.gui.activity.MainActivity

class ChoosePlaylistFragment(
    private val activity: MainActivity, private val musicId: Int
) : PlaylistsFragment(activity) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        setChooseMode(true)
        swapCursor(this.activity.getAllChoosePlaylists(musicId))
        return view
    }

    override fun onPlaylistClick(playlistId: Int) {
        this.activity.insertConnection(playlistId, musicId)
    }
}