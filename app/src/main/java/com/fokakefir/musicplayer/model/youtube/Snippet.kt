package com.fokakefir.musicplayer.model.youtube

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Snippet {
    @SerializedName("publishedAt")
    @Expose
    var publishedAt: String? = null

    @JvmField
    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @JvmField
    @SerializedName("channelTitle")
    @Expose
    var channelTitle: String? = null

    @JvmField
    @SerializedName("thumbnails")
    @Expose
    var thumbnails: ThumbnailsYT? = null

    constructor()
    constructor(
        publishedAt: String?,
        title: String?,
        description: String?,
        thumbnails: ThumbnailsYT?
    ) {
        this.publishedAt = publishedAt
        this.title = title
        this.description = description
        this.thumbnails = thumbnails
    }
}