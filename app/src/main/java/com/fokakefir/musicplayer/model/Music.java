package com.fokakefir.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Parcelable {

    private int id;
    private String videoId;
    private String title;
    private String artist;
    private int length;

    public Music() {
    }

    public Music(int id, String videoId, String title, String artist, int length) {
        this.id = id;
        this.videoId = videoId;
        this.title = title;
        this.artist = artist;
        this.length = length;
    }

    protected Music(Parcel in) {
        this.id = in.readInt();
        this.videoId = in.readString();
        this.title = in.readString();
        this.artist = in.readString();
        this.length = in.readInt();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.id);
        parcel.writeString(this.videoId);
        parcel.writeString(this.title);
        parcel.writeString(this.artist);
        parcel.writeInt(this.length);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
