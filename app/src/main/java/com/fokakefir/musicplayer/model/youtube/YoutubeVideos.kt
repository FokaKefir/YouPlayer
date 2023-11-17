package com.fokakefir.musicplayer.model.youtube

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class YoutubeVideos {
    @SerializedName("nextPageToken")
    @Expose
    var nextPageToken: String? = null

    @JvmField
    @SerializedName("items")
    @Expose
    var videos: List<VideoYT>? = null

    constructor()
    constructor(nextPageToken: String?, videos: List<VideoYT>?) {
        this.nextPageToken = nextPageToken
        this.videos = videos
    }
}