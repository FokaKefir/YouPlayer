package com.fokakefir.musicplayer.logic.network;

import com.fokakefir.musicplayer.model.youtube.YoutubeVideos;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public class YoutubeAPI {

    public static final int YOUTUBE_ITAG_VIDEO_480P = 18;
    public static final int YOUTUBE_ITAG_AUDIO_50K = 249;
    public static final int YOUTUBE_ITAG_AUDIO_160K = 251;
    public static final int YOUTUBE_ITAG_AUDIO_128K = 140;

    public static final String BASE_URL = "https://www.googleapis.com/youtube/v3/";
    public static final String SEARCH = "search";
    public static final String KEY = "?key=AIzaSyBja95PvyW-AFi3T2a8fuua8wDXwUEdcu0";
    public static final String CHANNEL_ID = "&channelId=UCR18dAVRt3-rYqFd3ac8zAg";
    public static final String MAX_RESULTS = "&maxResults=16";
    public static final String ORDER = "&order=relevance";
    public static final String PART = "&part=snippet";

    public static final String QUERY = "&q=";
    public static final String TYPE = "&type=video";

    public interface YoutubeVideosRequest {
        @GET
        Call<YoutubeVideos> getYT(@Url String url);
    }

    private static YoutubeVideosRequest request = null;

    public static YoutubeVideosRequest getRequest() {
        if (request == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            request = retrofit.create(YoutubeVideosRequest.class);
        }
        return request;
    }

}
