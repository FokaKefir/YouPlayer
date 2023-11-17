package com.fokakefir.musicplayer.logic.database;

import android.provider.BaseColumns;

public class MusicPlayerContract {

    private MusicPlayerContract() {}

    public static final class MusicEntry implements BaseColumns {
        public static final String TABLE_NAME = "musics";
        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_ARTIST = "artist";
        public static final String COLUMN_LENGTH = "length";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class PlaylistEntry implements BaseColumns {
        public static final String TABLE_NAME = "playlists";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_MUSICS = "musics";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class ConnectEntry implements BaseColumns {
        public static final String TABLE_NAME = "connect";
        public static final String COLUMN_PLAYLIST_ID = "playlist_id";
        public static final String COLUMN_MUSIC_ID = "music_id";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

}
