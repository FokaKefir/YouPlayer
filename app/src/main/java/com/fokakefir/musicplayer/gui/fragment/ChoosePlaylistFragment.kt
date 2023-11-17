package com.fokakefir.musicplayer.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fokakefir.musicplayer.gui.activity.MainActivity;

public class ChoosePlaylistFragment extends PlaylistsFragment {

    // region 1. Decl and Init

    private MainActivity activity;

    private int musicId;

    // endregion

    // region 2. Lifecycle and Constructor

    public ChoosePlaylistFragment(MainActivity activity, int musicId) {
        super(activity);
        this.activity = activity;
        this.musicId = musicId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setChooseMode(true);
        swapCursor(this.activity.getAllChoosePlaylists(this.musicId));
        return view;
    }

    // endregion

    // region 3. Playlist listener

    @Override
    public void onPlaylistClick(int playlistId) {
        this.activity.insertConnection(playlistId, this.musicId);
    }

    // endregion

}
