package com.fokakefir.musicplayer.gui.recyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnCreateContextMenuListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.gui.activity.MainActivity
import com.fokakefir.musicplayer.gui.recyclerview.PlaylistAdapter.PlaylistViewHolder
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract
import com.fokakefir.musicplayer.model.Playlist

class PlaylistAdapter(
    private var cursor: Cursor?,
    private val onPlaylistListener: OnPlaylistListener,
    private val context: Context?
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var options = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.example_playlist, parent, false)
        return PlaylistViewHolder(v, onPlaylistListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        if (!cursor!!.moveToPosition(position)) return
        holder.playlistId =
            cursor!!.getInt(cursor!!.getColumnIndex(MusicPlayerContract.PlaylistEntry._ID))
        holder.txtName.text =
            cursor!!.getString(cursor!!.getColumnIndex(MusicPlayerContract.PlaylistEntry.COLUMN_NAME))
        holder.txtSongs.text =
            cursor!!.getString(cursor!!.getColumnIndex(MusicPlayerContract.PlaylistEntry.COLUMN_MUSICS)) + " music"
        var color = R.color.playlistWhite
        when (cursor!!.getString(cursor!!.getColumnIndex(MusicPlayerContract.PlaylistEntry.COLUMN_COLOR))) {
            Playlist.COLOR_RED -> color = R.color.playlistRed
            Playlist.COLOR_ORANGE -> color = R.color.playlistOrange
            Playlist.COLOR_YELLOW -> color = R.color.playlistYellow
            Playlist.COLOR_GREEN -> color = R.color.playlistGreen
            Playlist.COLOR_BLUE -> color = R.color.playlistBlue
        }

        holder.imgPlaylist.setBackgroundColor(context!!.resources.getColor(color))
        holder.options = options
        holder.name =
            cursor!!.getString(cursor!!.getColumnIndex(MusicPlayerContract.PlaylistEntry.COLUMN_NAME))
        holder.color =
            cursor!!.getString(cursor!!.getColumnIndex(MusicPlayerContract.PlaylistEntry.COLUMN_COLOR))
    }

    override fun getItemCount(): Int {
        return cursor!!.count
    }

    fun setOptions(options: Boolean) {
        this.options = options
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

    // endregion
    // region 4. Holder class
    inner class PlaylistViewHolder(itemView: View, onPlaylistListener: OnPlaylistListener) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener, OnCreateContextMenuListener,
        MenuItem.OnMenuItemClickListener {
        var imgPlaylist: ImageView
        var txtName: TextView
        var txtSongs: TextView
        var playlistId = 0
        var name: String? = null
        var color: String? = null
        var options = true
        private val onPlaylistListener: OnPlaylistListener

        init {
            imgPlaylist = itemView.findViewById(R.id.img_playlist)
            txtName = itemView.findViewById(R.id.txt_playlist_name)
            txtSongs = itemView.findViewById(R.id.txt_playlist_songs)
            this.itemView.setOnClickListener(this)
            this.itemView.setOnCreateContextMenuListener(this)
            this.onPlaylistListener = onPlaylistListener
        }

        override fun onClick(view: View) {
            if (view === this.itemView) {
                this.onPlaylistListener.onPlaylistClick(playlistId)
            }
        }

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenuInfo?) {
            if (!this.options || playlistId == MainActivity.DEFAULT_PLAYLIST_ID) return
            menu?.setHeaderTitle("Options")
            val itemEdit = menu?.add(Menu.NONE, MENU_ITEM_EDIT_ID, 1, "Edit playlist")
            val itemDelete =
                menu?.add(Menu.NONE, MENU_ITEM_DELETE_ID, 2, "Delete playlist")
            itemEdit?.setOnMenuItemClickListener(this)
            itemDelete?.setOnMenuItemClickListener(this)
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            return when (item.itemId) {
                MENU_ITEM_EDIT_ID -> {
                    val name: String = name ?: ""
                    val color: String = color ?: ""
                    this.onPlaylistListener.onEditPlaylistClick(playlistId, name, color)
                    true
                }

                MENU_ITEM_DELETE_ID -> {
                    this.onPlaylistListener.onDeletePlaylistClick(playlistId)
                    true
                }

                else -> false
            }
        }


        private val MENU_ITEM_EDIT_ID = 1
        private val MENU_ITEM_DELETE_ID = 2

    }

    // endregion
    // region 5. Listener interface
    interface OnPlaylistListener {
        fun onPlaylistClick(playlistId: Int)
        fun onEditPlaylistClick(playlistId: Int, name: String, color: String)
        fun onDeletePlaylistClick(playlistId: Int)
    } // endregion
}