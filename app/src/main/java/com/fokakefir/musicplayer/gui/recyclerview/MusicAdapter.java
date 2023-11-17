package com.fokakefir.musicplayer.gui.recyclerview;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fokakefir.musicplayer.R;
import com.fokakefir.musicplayer.gui.activity.MainActivity;
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract;
import com.fokakefir.musicplayer.model.Music;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    // region 1. Decl and Init

    private Cursor cursor;
    private OnMusicListener onMusicListener;
    private int playlistId;

    // endregion

    // region 2. Constructor

    public MusicAdapter(Cursor cursor, OnMusicListener onMusicListener, int playlistId) {
        this.cursor = cursor;
        this.onMusicListener = onMusicListener;
        this.playlistId = playlistId;
    }

    // endregion

    // region 3. Adapter

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.example_music, parent, false);
        MusicViewHolder viewHolder = new MusicViewHolder(v, this.onMusicListener);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MusicAdapter.MusicViewHolder holder, int position) {
        if (!this.cursor.moveToPosition(position))
            return;

        Music currentMusic = new Music(
                this.cursor.getInt(this.cursor.getColumnIndex(MusicPlayerContract.MusicEntry._ID)),
                this.cursor.getString(this.cursor.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_VIDEO_ID)),
                this.cursor.getString(this.cursor.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_TITLE)),
                this.cursor.getString(this.cursor.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_ARTIST)),
                this.cursor.getInt(this.cursor.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_LENGTH))
        );

        holder.txtTitle.setText(currentMusic.getTitle());
        holder.txtArtist.setText(currentMusic.getArtist());

        if (currentMusic.getLength() % 60 > 9)
            holder.txtLength.setText((currentMusic.getLength() / 60) + ":" + (currentMusic.getLength() % 60));
        else
            holder.txtLength.setText((currentMusic.getLength() / 60) + ":0" + (currentMusic.getLength() % 60));

        holder.music = currentMusic;
    }

    @Override
    public int getItemCount() {
        return this.cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (this.cursor != null) {
            this.cursor.close();
        }
        this.cursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    // endregion

    // region 4. Holder class

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        private static final int MENU_ITEM_QUEUE_ID = 0;
        private static final int MENU_ITEM_ADD_ID = 1;
        private static final int MENU_ITEM_REMOVE_ID = 2;
        private static final int MENU_ITEM_DELETE_ID = 3;

        public TextView txtTitle;
        public TextView txtArtist;
        public TextView txtLength;

        public Music music;

        private View itemView;

        private OnMusicListener onMusicListener;

        public MusicViewHolder(@NonNull View itemView, OnMusicListener onMusicListener) {
            super(itemView);

            this.music = null;

            this.txtTitle = itemView.findViewById(R.id.txt_music_title);
            this.txtArtist = itemView.findViewById(R.id.txt_music_artist);
            this.txtLength = itemView.findViewById(R.id.txt_music_length);

            this.itemView = itemView;
            this.itemView.setOnClickListener(this);
            this.itemView.setOnCreateContextMenuListener(this);

            this.onMusicListener = onMusicListener;
        }

        @Override
        public void onClick(View view) {
            if (view == this.itemView) {
                this.onMusicListener.onMusicClick(this.music);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            menu.setHeaderTitle("Options");

            MenuItem itemQueue = menu.add(Menu.NONE, MENU_ITEM_QUEUE_ID, 1, "Add to queue");
            MenuItem itemAdd = menu.add(Menu.NONE, MENU_ITEM_ADD_ID, 2, "Add to playlist");
            itemQueue.setOnMenuItemClickListener(this);
            itemAdd.setOnMenuItemClickListener(this);

            if (playlistId == MainActivity.DEFAULT_PLAYLIST_ID) {
                MenuItem itemDelete = menu.add(Menu.NONE, MENU_ITEM_DELETE_ID, 3, "Delete music");
                itemDelete.setOnMenuItemClickListener(this);
            } else {
                MenuItem itemRemove = menu.add(Menu.NONE, MENU_ITEM_REMOVE_ID, 3, "Remove from playlist");
                itemRemove.setOnMenuItemClickListener(this);
            }
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case MENU_ITEM_QUEUE_ID:
                    this.onMusicListener.onAddToQueueMusicClick(this.music);
                    return true;
                case MENU_ITEM_ADD_ID:
                    this.onMusicListener.onAddMusicClick(this.music);
                    return true;
                case MENU_ITEM_REMOVE_ID:
                    this.onMusicListener.onRemoveMusicClick(this.music);
                    return true;
                case MENU_ITEM_DELETE_ID:
                    this.onMusicListener.onDeleteMusicClick(this.music);
                    return true;
                default:
                    return false;
            }
        }
    }

    // endregion

    // region 5. Listener interface

    public interface OnMusicListener {
        void onMusicClick(Music music);
        void onAddToQueueMusicClick(Music music);
        void onAddMusicClick(Music music);
        void onRemoveMusicClick(Music music);
        void onDeleteMusicClick(Music music);
    }

    // endregion

}
