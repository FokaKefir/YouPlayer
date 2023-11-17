package com.fokakefir.musicplayer.logic.database

import android.provider.BaseColumns

class MusicPlayerContract private constructor() {
    object MusicEntry : BaseColumns {
        const val TABLE_NAME = "musics"
        const val _ID = "_id"
        const val COLUMN_VIDEO_ID = "video_id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_LENGTH = "length"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    object PlaylistEntry : BaseColumns {
        const val TABLE_NAME = "playlists"
        const val _ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_COLOR = "color"
        const val COLUMN_MUSICS = "musics"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    object ConnectEntry : BaseColumns {
        const val TABLE_NAME = "connect"
        const val COLUMN_PLAYLIST_ID = "playlist_id"
        const val COLUMN_MUSIC_ID = "music_id"
        const val COLUMN_TIMESTAMP = "timestamp"
    }
}