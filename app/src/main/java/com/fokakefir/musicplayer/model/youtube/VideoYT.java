package com.fokakefir.musicplayer.model.youtube;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VideoYT {

    @SerializedName("id")
    @Expose
    private VideoId id;

    @SerializedName("snippet")
    @Expose
    private Snippet snippet;

    public VideoYT() {
    }

    public VideoYT(VideoId id, Snippet snippet) {
        this.id = id;
        this.snippet = snippet;
    }

    public VideoId getId() {
        return id;
    }

    public void setId(VideoId id) {
        this.id = id;
    }

    public Snippet getSnippet() {
        return snippet;
    }

    public void setSnippet(Snippet snippet) {
        this.snippet = snippet;
    }
}
