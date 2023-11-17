package com.fokakefir.musicplayer.model.youtube

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class VideoYT {
    @JvmField
    @SerializedName("id")
    @Expose
    var id: VideoId? = null

    @JvmField
    @SerializedName("snippet")
    @Expose
    var snippet: Snippet? = null

    constructor()
    constructor(id: VideoId?, snippet: Snippet?) {
        this.id = id
        this.snippet = snippet
    }
}