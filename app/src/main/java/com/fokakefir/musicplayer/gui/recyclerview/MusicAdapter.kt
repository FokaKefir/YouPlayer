package com.fokakefir.musicplayer.gui.recyclerview

import android.annotation.SuppressLint
import android.database.Cursor
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.activity.MainActivity
import com.fokakefir.musicplayer.gui.recyclerview.MusicAdapter.MusicViewHolder
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract
import com.fokakefir.musicplayer.model.Music

class MusicAdapter(
    private var cursor: Cursor?,
    private val onMusicListener: OnMusicListener,
    private val playlistId: Int
) : RecyclerView.Adapter<MusicViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.example_music, parent, false)
        return MusicViewHolder(v, onMusicListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        if (!cursor!!.moveToPosition(position)) return
        val currentMusic = Music(
            cursor!!.getInt(cursor!!.getColumnIndex(MusicPlayerContract.MusicEntry._ID)),
            cursor!!.getString(cursor!!.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_VIDEO_ID)),
            cursor!!.getString(cursor!!.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_TITLE)),
            cursor!!.getString(cursor!!.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_ARTIST)),
            cursor!!.getInt(cursor!!.getColumnIndex(MusicPlayerContract.MusicEntry.COLUMN_LENGTH))
        )
        holder.txtTitle.text = currentMusic.title
        holder.txtArtist.text = currentMusic.artist
        if (currentMusic.length % 60 > 9) holder.txtLength.text =
            (currentMusic.length / 60).toString() + ":" + currentMusic.length % 60 else holder.txtLength.text =
            (currentMusic.length / 60).toString() + ":0" + currentMusic.length % 60
        holder.music = currentMusic
    }

    override fun getItemCount(): Int {
        return cursor!!.count
    }

    fun swapCursor(newCursor: Cursor?) {
        if (cursor != null) {
            cursor!!.close()
        }
        cursor = newCursor
        if (newCursor != null) {
            notifyDataSetChanged()
        }
    }

    inner class MusicViewHolder(iv: View, onMusicListener: OnMusicListener) :
        RecyclerView.ViewHolder(iv), View.OnClickListener, OnCreateContextMenuListener,
        MenuItem.OnMenuItemClickListener {
        var txtTitle: TextView
        var txtArtist: TextView
        var txtLength: TextView
        var music: Music? = null
        private val onMusicListener: OnMusicListener

        init {
            txtTitle = iv.findViewById(R.id.txt_music_title)
            txtArtist = iv.findViewById(R.id.txt_music_artist)
            txtLength = iv.findViewById(R.id.txt_music_length)
            iv.setOnClickListener(this)
            iv.setOnCreateContextMenuListener(this)
            this.onMusicListener = onMusicListener
        }

        override fun onClick(view: View) {
            if (view === this.itemView) {
                val music: Music = this.music ?: Music()
                this.onMusicListener.onMusicClick(music)
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
            menu?.setHeaderTitle("Options")
            val itemQueue = menu?.add(Menu.NONE, MENU_ITEM_QUEUE_ID, 1, "Add to queue")
            val itemAdd = menu?.add(Menu.NONE, MENU_ITEM_ADD_ID, 2, "Add to playlist")
            itemQueue?.setOnMenuItemClickListener(this)
            itemAdd?.setOnMenuItemClickListener(this)
            if (playlistId == MainActivity.DEFAULT_PLAYLIST_ID) {
                val itemDelete =
                    menu?.add(Menu.NONE, MENU_ITEM_DELETE_ID, 3, "Delete music")
                itemDelete?.setOnMenuItemClickListener(this)
            } else {
                val itemRemove =
                    menu?.add(Menu.NONE, MENU_ITEM_REMOVE_ID, 3, "Remove from playlist")
                itemRemove?.setOnMenuItemClickListener(this)
            }
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            val music: Music = this.music ?: Music()
            return when (item.itemId) {
                MENU_ITEM_QUEUE_ID -> {
                    this.onMusicListener.onAddToQueueMusicClick(music)
                    true
                }

                MENU_ITEM_ADD_ID -> {
                    this.onMusicListener.onAddMusicClick(music)
                    true
                }

                MENU_ITEM_REMOVE_ID -> {
                    this.onMusicListener.onRemoveMusicClick(music)
                    true
                }

                MENU_ITEM_DELETE_ID -> {
                    this.onMusicListener.onDeleteMusicClick(music)
                    true
                }

                else -> false
            }
        }

        private val MENU_ITEM_QUEUE_ID = 0
        private val MENU_ITEM_ADD_ID = 1
        private val MENU_ITEM_REMOVE_ID = 2
        private val MENU_ITEM_DELETE_ID = 3

    }

    interface OnMusicListener {
        fun onMusicClick(music: Music)
        fun onAddToQueueMusicClick(music: Music)
        fun onAddMusicClick(music: Music)
        fun onRemoveMusicClick(music: Music)
        fun onDeleteMusicClick(music: Music)
    }
}