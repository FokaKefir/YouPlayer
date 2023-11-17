package com.fokakefir.musicplayer.gui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract;
import com.fokakefir.musicplayer.model.Playlist;

public class PlaylistDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {

    private EditText txtPlaylist;
    private Spinner spinner;
    private ImageView imgPlaylist;

    private String strName;
    private String strColor;

    private int playlistId;

    private boolean edit;

    private OnPlaylistDialogListener listener;

    public PlaylistDialog(OnPlaylistDialogListener listener) {
        this.listener = listener;
        this.edit = false;
    }

    public PlaylistDialog(int playlistId, String strName, String strColor, OnPlaylistDialogListener listener) {
        this.playlistId = playlistId;
        this.strName = strName;
        this.strColor = strColor;
        this.listener = listener;
        this.edit = true;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_playlist, null);

        builder.setView(view)
                .setTitle("Add new playlist")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        strName = txtPlaylist.getText().toString().trim();
                        if (!edit) {
                            listener.onPlaylistCreate(strName, strColor);
                        } else {
                            listener.onPlaylistEdit(playlistId, strName, strColor);
                        }
                    }
                });

        this.txtPlaylist = view.findViewById(R.id.txt_new_playlist);
        this.spinner = view.findViewById(R.id.spinner_colors);
        this.imgPlaylist = view.findViewById(R.id.img_new_playlist);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.colors, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner.setAdapter(adapter);
        this.spinner.setOnItemSelectedListener(this);

        setImgPlaylistColor(Playlist.COLOR_RED);

        if (this.strName != null && this.strColor != null) {
            this.txtPlaylist.setText(strName);
            int position = adapter.getPosition(this.strColor);
            this.spinner.setSelection(position);
            setImgPlaylistColor(this.strColor);
        }

        return builder.create();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        this.strColor = parent.getItemAtPosition(position).toString();
        setImgPlaylistColor(this.strColor);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setImgPlaylistColor(String strColor) {
        int color = R.color.playlistWhite;
        switch (strColor) {
            case Playlist.COLOR_RED:
                color = R.color.playlistRed;
                break;
            case Playlist.COLOR_ORANGE:
                color = R.color.playlistOrange;
                break;
            case Playlist.COLOR_YELLOW:
                color = R.color.playlistYellow;
                break;
            case Playlist.COLOR_GREEN:
                color = R.color.playlistGreen;
                break;
            case Playlist.COLOR_BLUE:
                color = R.color.playlistBlue;
                break;
        }
        this.imgPlaylist.setBackgroundColor(this.getContext().getResources().getColor(color));
    }

    public interface OnPlaylistDialogListener {
        void onPlaylistCreate(String name, String color);
        void onPlaylistEdit(int playlistId, String name, String color);
    }

}
