package com.fokakefir.musicplayer.gui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.gui.recyclerview.MusicAdapter;
import com.fokakefir.musicplayer.model.Music;

import java.util.List;

public class MusicsFragment extends Fragment implements MusicAdapter.OnMusicListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private MainActivity activity;

    private int playlistId;

    private RecyclerView recyclerView;
    private MusicAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // endregion

    // region 2. Lifecycle and Constructor

    public MusicsFragment(MainActivity activity, int playlistId) {
        this.activity = activity;
        this.playlistId = playlistId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_musics, container, false);

        Cursor cursor = null;
        if (this.playlistId == MainActivity.DEFAULT_PLAYLIST_ID) {
            cursor = this.activity.getAllMusic();
        } else {
            cursor = this.activity.getAllMusic(this.playlistId);
        }

        this.recyclerView = this.view.findViewById(R.id.recycler_view_musics);
        this.layoutManager = new LinearLayoutManager(getContext());
        this.adapter = new MusicAdapter(cursor, this, this.playlistId);
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        return this.view;
    }

    // endregion

    // region 3. Music listener

    @Override
    public void onMusicClick(Music music) {
        this.activity.playMusic(music, this.playlistId);
    }

    @Override
    public void onAddToQueueMusicClick(Music music) {
        this.activity.addMusicToQueue(music);
    }

    @Override
    public void onAddMusicClick(Music music) {
        this.activity.addChoosePlaylistFragment(music.getId());
    }

    @Override
    public void onRemoveMusicClick(Music music) {
        this.activity.deleteConnection(this.playlistId, music.getId());
    }

    @Override
    public void onDeleteMusicClick(Music music) {
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    activity.deleteMusicFromStorage(music);
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogListener)
                .setNegativeButton("No", dialogListener)
                .show();
    }

    // endregion

    // region 4. Database

    public void swapCursor(Cursor cursor) {
        this.adapter.swapCursor(cursor);
    }

    // endregion

    // region 5. Getters and Setters

    public int getPlaylistId() {
        return playlistId;
    }

    // endregion


}
