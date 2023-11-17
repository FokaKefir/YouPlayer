package com.fokakefir.musicplayer.model.youtube

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VideoId {
    @JvmField
    @SerializedName("videoId")
    @Expose
    var videoId: String? = null

    constructor()
    constructor(videoId: String?) {
        this.videoId = videoId
    }
}