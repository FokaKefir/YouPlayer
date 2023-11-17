package com.fokakefir.musicplayer.model.youtube;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoId {

    @SerializedName("videoId")
    @Expose
    private String videoId;

    public VideoId() {
    }

    public VideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }
}
