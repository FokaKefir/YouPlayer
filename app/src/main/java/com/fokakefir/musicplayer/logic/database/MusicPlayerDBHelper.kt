package com.fokakefir.musicplayer.logic.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract.ConnectEntry
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract.MusicEntry
import com.fokakefir.musicplayer.logic.database.MusicPlayerContract.PlaylistEntry
import com.fokakefir.musicplayer.model.Playlist

class MusicPlayerDBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(database: SQLiteDatabase) {
        val SQL_CREATE_MUSICS_TABLE = "CREATE TABLE " +
                MusicEntry.TABLE_NAME + " (" +
                MusicEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MusicEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL, " +
                MusicEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MusicEntry.COLUMN_ARTIST + " TEXT NOT NULL, " +
                MusicEntry.COLUMN_LENGTH + " INTEGER NOT NULL, " +
                MusicEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");"
        val SQL_CREATE_PLAYLISTS_TABLE = "CREATE TABLE " +
                PlaylistEntry.TABLE_NAME + " (" +
                PlaylistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PlaylistEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                PlaylistEntry.COLUMN_COLOR + " TEXT NOT NULL, " +
                PlaylistEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");"
        val SQL_CREATE_CONNECT_TABLE = "CREATE TABLE " +
                ConnectEntry.TABLE_NAME + " (" +
                ConnectEntry.COLUMN_PLAYLIST_ID + " INTEGER NOT NULL, " +
                ConnectEntry.COLUMN_MUSIC_ID + " INTEGER NOT NULL, " +
                ConnectEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");"
        val SQL_INSERT_DEFAULT_PLAYLIST = "INSERT INTO " +
                PlaylistEntry.TABLE_NAME + " (" +
                PlaylistEntry.COLUMN_NAME + ", " +
                PlaylistEntry.COLUMN_COLOR + " ) " +
                "VALUES('All music', '" +
                Playlist.COLOR_RED + "');"
        database.execSQL(SQL_CREATE_MUSICS_TABLE)
        database.execSQL(SQL_CREATE_PLAYLISTS_TABLE)
        database.execSQL(SQL_CREATE_CONNECT_TABLE)
        database.execSQL(SQL_INSERT_DEFAULT_PLAYLIST)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        database.execSQL("DROP TABLE IF EXISTS " + MusicEntry.TABLE_NAME)
        database.execSQL("DROP TABLE IF EXISTS " + PlaylistEntry.TABLE_NAME)
        database.execSQL("DROP TABLE IF EXISTS " + ConnectEntry.TABLE_NAME)
        onCreate(database)
    }

    companion object {
        const val DATABASE_NAME = "music_player.db"
        const val DATABASE_VERSION = 1
    }
}