package com.fokakefir.musicplayer.model

import android.os.Parcel
import android.os.Parcelable

class Music : Parcelable {
    @JvmField
    var id = 0
    @JvmField
    var videoId: String? = null
    @JvmField
    var title: String? = null
    @JvmField
    var artist: String? = null
    @JvmField
    var length = 0

    constructor()
    constructor(id: Int, videoId: String?, title: String?, artist: String?, length: Int) {
        this.id = id
        this.videoId = videoId
        this.title = title
        this.artist = artist
        this.length = length
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readInt()
        videoId = `in`.readString()
        title = `in`.readString()
        artist = `in`.readString()
        length = `in`.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(videoId)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeInt(length)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Music?> = object : Parcelable.Creator<Music?> {
            override fun createFromParcel(`in`: Parcel): Music? {
                return Music(`in`)
            }

            override fun newArray(size: Int): Array<Music?> {
                return arrayOfNulls(size)
            }
        }
    }
}