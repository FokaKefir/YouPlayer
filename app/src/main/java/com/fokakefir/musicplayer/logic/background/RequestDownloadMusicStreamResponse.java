package com.fokakefir.musicplayer.logic.background;

public interface RequestDownloadMusicStreamResponse {
    void onMusicDownloaded(String videoId, String title, String artist, int length);
}
