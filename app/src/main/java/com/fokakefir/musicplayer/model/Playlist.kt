package com.fokakefir.musicplayer.model

class Playlist {
    var id = 0
    var name: String? = null
    var numberOfMusics = 0
    var color: String? = null

    constructor()
    constructor(id: Int, name: String?, numberOfMusics: Int, color: String?) {
        this.id = id
        this.name = name
        this.numberOfMusics = numberOfMusics
        this.color = color
    }

    companion object {
        const val COLOR_RED = "red"
        const val COLOR_ORANGE = "orange"
        const val COLOR_YELLOW = "yellow"
        const val COLOR_GREEN = "green"
        const val COLOR_BLUE = "blue"
    }
}