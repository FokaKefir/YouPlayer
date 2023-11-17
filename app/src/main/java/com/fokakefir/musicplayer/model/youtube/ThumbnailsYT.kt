package com.fokakefir.musicplayer.model.youtube

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ThumbnailsYT {
    @JvmField
    @SerializedName("medium")
    @Expose
    var medium: MediumThumbnail? = null

    constructor()
    constructor(medium: MediumThumbnail?) {
        this.medium = medium
    }

    inner class MediumThumbnail {
        @JvmField
        @SerializedName("url")
        @Expose
        var url: String? = null

        constructor()
        constructor(url: String?) {
            this.url = url
        }
    }
}