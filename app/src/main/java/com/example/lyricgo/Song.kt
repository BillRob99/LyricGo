package com.example.lyricgo

import java.io.Serializable

/**
 * This class represents a Song.
 * @author William Roberts
 * @version 1.0
 *
 * @param name  The name of the song.
 * @param artist The artist who sang the song.
 * @param songPack The pack that the song is from.
 * @param lyrics All of the lyrics from the song.
 * @param filename The name of the song in the assets file.
 */
class Song(name:String, artist:String, songPack:String, lyrics:String, filename:String): Serializable {
    private val name:String
    private val artist:String
    private val songPack:String
    private val lyrics:String
    private val filename:String

    //Assigns variables for the song.
    init{
        this.name = name
        this.artist = artist
        this.songPack = songPack
        this.lyrics = lyrics
        this.filename = filename
    }

    fun getName(): String {
        return this.name
    }

    fun getArtist(): String {
        return this.artist
    }

    fun getSongPack(): String {
        return this.songPack
    }

    fun getLyrics(): String {
        return this.lyrics
    }

    fun getFilename(): String {
        return this.filename
    }
}