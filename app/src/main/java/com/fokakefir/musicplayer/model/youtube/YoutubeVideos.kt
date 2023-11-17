package com.fokakefir.musicplayer.model.youtube;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YoutubeVideos {
    @SerializedName("nextPageToken")
    @Expose
    private String nextPageToken;

    @SerializedName("items")
    @Expose
    private List<VideoYT> videos;

    public YoutubeVideos() {
    }

    public YoutubeVideos(String nextPageToken, List<VideoYT> videos) {
        this.nextPageToken = nextPageToken;
        this.videos = videos;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<VideoYT> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoYT> videos) {
        this.videos = videos;
    }
}
