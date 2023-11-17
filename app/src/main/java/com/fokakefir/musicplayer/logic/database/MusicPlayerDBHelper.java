package com.fokakefir.musicplayer.logic.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.fokakefir.musicplayer.logic.database.MusicPlayerContract.*;
import com.fokakefir.musicplayer.model.Playlist;

public class MusicPlayerDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "music_player.db";
    public static final int DATABASE_VERSION = 1;

    public MusicPlayerDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        final String SQL_CREATE_MUSICS_TABLE = "CREATE TABLE " +
                MusicEntry.TABLE_NAME + " (" +
                MusicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MusicEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                MusicEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MusicEntry.COLUMN_ARTIST + " TEXT NOT NULL, " +
                MusicEntry.COLUMN_LENGTH + " INTEGER NOT NULL, " +
                MusicEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        final String SQL_CREATE_PLAYLISTS_TABLE = "CREATE TABLE " +
                PlaylistEntry.TABLE_NAME + " (" +
                PlaylistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlaylistEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlaylistEntry.COLUMN_COLOR + " TEXT NOT NULL, " +
                PlaylistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        final String SQL_CREATE_CONNECT_TABLE = "CREATE TABLE " +
                ConnectEntry.TABLE_NAME + " (" +
                ConnectEntry.COLUMN_PLAYLIST_ID + " INTEGER NOT NULL, " +
                ConnectEntry.COLUMN_MUSIC_ID + " INTEGER NOT NULL, " +
                ConnectEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        final String SQL_INSERT_DEFAULT_PLAYLIST = "INSERT INTO " +
                PlaylistEntry.TABLE_NAME + " (" +
                PlaylistEntry.COLUMN_NAME + ", " +
                PlaylistEntry.COLUMN_COLOR + " ) " +
                "VALUES('All music', '" +
                Playlist.COLOR_RED + "');";

        database.execSQL(SQL_CREATE_MUSICS_TABLE);
        database.execSQL(SQL_CREATE_PLAYLISTS_TABLE);
        database.execSQL(SQL_CREATE_CONNECT_TABLE);
        database.execSQL(SQL_INSERT_DEFAULT_PLAYLIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + MusicEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + PlaylistEntry.TABLE_NAME);
        database.execSQL("DROP TABLE IF EXISTS " + ConnectEntry.TABLE_NAME);
        onCreate(database);
    }
}
