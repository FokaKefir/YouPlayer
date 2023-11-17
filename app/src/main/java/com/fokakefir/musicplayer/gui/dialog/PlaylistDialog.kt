package com.fokakefir.musicplayer.gui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDialogFragment
import com.fokakefir.musicplayer.R
import com.fokakefir.musicplayer.model.Playlist

class PlaylistDialog : AppCompatDialogFragment, OnItemSelectedListener {
    private lateinit var txtPlaylist: EditText
    private lateinit var spinner: Spinner
    private var imgPlaylist: ImageView? = null
    private var strName: String? = null
    private var strColor: String? = null
    private var playlistId = 0
    private var edit: Boolean
    private var listener: OnPlaylistDialogListener

    constructor(listener: OnPlaylistDialogListener) {
        this.listener = listener
        edit = false
    }

    constructor(
        playlistId: Int,
        strName: String?,
        strColor: String?,
        listener: OnPlaylistDialogListener
    ) {
        this.playlistId = playlistId
        this.strName = strName
        this.strColor = strColor
        this.listener = listener
        edit = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_playlist, null)
        builder.setView(view)
            .setTitle("Add new playlist")
            .setNegativeButton("cancel") { dialogInterface, i -> }
            .setPositiveButton("ok") { dialogInterface, i ->
                strName = txtPlaylist!!.text.toString().trim { it <= ' ' }
                if (!edit) {
                    listener.onPlaylistCreate(strName, strColor)
                } else {
                    listener.onPlaylistEdit(playlistId, strName, strColor)
                }
            }
        txtPlaylist = view.findViewById(R.id.txt_new_playlist)
        spinner = view.findViewById(R.id.spinner_colors)
        imgPlaylist = view.findViewById(R.id.img_new_playlist)
        val adapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.colors,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = this
        setImgPlaylistColor(Playlist.COLOR_RED)
        if (strName != null && strColor != null) {
            txtPlaylist.text = Editable.Factory.getInstance().newEditable(strName)
            val position = adapter.getPosition(strColor)
            spinner.setSelection(position)
            setImgPlaylistColor(strColor)
        }
        return builder.create()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, l: Long) {
        strColor = parent.getItemAtPosition(position).toString()
        setImgPlaylistColor(strColor)
    }

    override fun onNothingSelected(adapterView: AdapterView<*>?) {}
    fun setImgPlaylistColor(strColor: String?) {
        var color = R.color.playlistWhite
        when (strColor) {
            Playlist.COLOR_RED -> color = R.color.playlistRed
            Playlist.COLOR_ORANGE -> color = R.color.playlistOrange
            Playlist.COLOR_YELLOW -> color = R.color.playlistYellow
            Playlist.COLOR_GREEN -> color = R.color.playlistGreen
            Playlist.COLOR_BLUE -> color = R.color.playlistBlue
        }
        imgPlaylist!!.setBackgroundColor(this.context!!.resources.getColor(color))
    }

    interface OnPlaylistDialogListener {
        fun onPlaylistCreate(name: String?, color: String?)
        fun onPlaylistEdit(playlistId: Int, name: String?, color: String?)
    }
}