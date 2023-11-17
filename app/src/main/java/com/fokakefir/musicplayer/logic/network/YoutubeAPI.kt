package com.fokakefir.musicplayer.logic.network

import com.fokakefir.musicplayer.model.youtube.YoutubeVideos
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

object YoutubeAPI {
    const val YOUTUBE_ITAG_VIDEO_480P = 18
    const val YOUTUBE_ITAG_AUDIO_50K = 249
    const val YOUTUBE_ITAG_AUDIO_160K = 251
    const val YOUTUBE_ITAG_AUDIO_128K = 140
    const val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    const val SEARCH = "search"
    const val KEY = "?key=AIzaSyBja95PvyW-AFi3T2a8fuua8wDXwUEdcu0"
    const val CHANNEL_ID = "&channelId=UCR18dAVRt3-rYqFd3ac8zAg"
    const val MAX_RESULTS = "&maxResults=16"
    const val ORDER = "&order=relevance"
    const val PART = "&part=snippet"
    const val QUERY = "&q="
    const val TYPE = "&type=video"
    @JvmStatic
    var request: YoutubeVideosRequest? = null
        get() {
            if (field == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                field = retrofit.create(YoutubeVideosRequest::class.java)
            }
            return field
        }
        private set

    interface YoutubeVideosRequest {
        @GET
        fun getYT(@Url url: String?): Call<YoutubeVideos?>?
    }
}