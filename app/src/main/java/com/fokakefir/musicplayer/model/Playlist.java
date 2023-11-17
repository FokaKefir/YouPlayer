package com.fokakefir.musicplayer.model;

import android.graphics.Color;

public class Playlist {

    public static final String COLOR_RED = "red";
    public static final String COLOR_ORANGE = "orange";
    public static final String COLOR_YELLOW = "yellow";
    public static final String COLOR_GREEN = "green";
    public static final String COLOR_BLUE = "blue";

    private int id;
    private String name;
    private int numberOfMusics;
    private String color;

    public Playlist() {
    }

    public Playlist(int id, String name, int numberOfMusics, String color) {
        this.id = id;
        this.name = name;
        this.numberOfMusics = numberOfMusics;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfMusics() {
        return numberOfMusics;
    }

    public void setNumberOfMusics(int numberOfMusics) {
        this.numberOfMusics = numberOfMusics;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
