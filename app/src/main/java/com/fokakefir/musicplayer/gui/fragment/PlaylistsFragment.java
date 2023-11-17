package com.fokakefir.musicplayer.gui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.gui.dialog.PlaylistDialog;
import com.fokakefir.musicplayer.gui.recyclerview.PlaylistAdapter;
import com.fokakefir.musicplayer.model.Playlist;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class PlaylistsFragment extends Fragment implements PlaylistAdapter.OnPlaylistListener, View.OnClickListener, PlaylistDialog.OnPlaylistDialogListener {

    // region 0. Constants

    // endregion

    // region 1. Decl and Init

    private View view;

    private MainActivity activity;

    private RecyclerView recyclerView;
    private PlaylistAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private FloatingActionButton fabAddPlaylist;

    // endregion

    // region 2. Lifecycle and Constructor

    public PlaylistsFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_playlists, container, false);

        this.recyclerView = this.view.findViewById(R.id.recycler_view_playlists);
        this.layoutManager = new LinearLayoutManager(getContext());
        this.adapter = new PlaylistAdapter(this.activity.getAllPlaylists(), this, getContext());
        this.recyclerView.setLayoutManager(this.layoutManager);
        this.recyclerView.setAdapter(this.adapter);

        this.fabAddPlaylist = this.view.findViewById(R.id.fab_add_playlist);
        this.fabAddPlaylist.setOnClickListener(this);

        return this.view;
    }

    // endregion

    // region 3. Playlist listener

    @Override
    public void onPlaylistClick(int playlistId) {
        this.activity.addMusicsFragment(playlistId);
    }

    @Override
    public void onEditPlaylistClick(int playlistId, String name, String color) {
        PlaylistDialog dialog = new PlaylistDialog(playlistId, name, color, this);
        dialog.show(this.activity.getSupportFragmentManager(), "playlist dialog");
    }

    @Override
    public void onDeletePlaylistClick(int playlistId) {
        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    activity.deletePlaylist(playlistId);
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

    // region 4. Floating Button listener

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_add_playlist) {
            PlaylistDialog dialog = new PlaylistDialog(this);
            dialog.show(this.activity.getSupportFragmentManager(), "playlist dialog");
        }
    }

    // endregion

    // region 4. Database

    public void swapCursor(Cursor cursor) {
        this.adapter.swapCursor(cursor);
    }

    @Override
    public void onPlaylistCreate(String name, String color) {
        if (!name.isEmpty())
            this.activity.insertPlaylist(name, color);
        else
            Toast.makeText(this.activity, "Name can't be empty", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlaylistEdit(int playlistId, String name, String color) {
        if (!name.isEmpty())
            this.activity.updatePlaylist(playlistId, name, color);
        else
            Toast.makeText(this.activity, "Name can't be empty", Toast.LENGTH_SHORT).show();
    }

    // endregion

    // region 5. Getters and Setters

    public void setChooseMode(boolean condition) {
        if (condition) {
            this.fabAddPlaylist.setVisibility(View.GONE);
            this.adapter.setOptions(false);
        } else {
            this.fabAddPlaylist.setVisibility(View.VISIBLE);
            this.adapter.setOptions(true);
        }
        this.adapter.notifyDataSetChanged();
    }

    // endregion

}