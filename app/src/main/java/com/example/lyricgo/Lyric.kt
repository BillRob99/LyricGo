package com.example.lyricgo

import java.io.Serializable

/**
 * This class represents a lyric.
 * @author William Roberts
 * @version 1.0
 *
 * @param words The words of the lyric.
 */
class Lyric(words: String) : Serializable {
    private val words: String
    private var found = false

    //Assigns the words for the Lyric.
    init {
        this.words = words
    }

    fun getWords(): String {
        return words
    }

    fun getFound(): Boolean {
        return found
    }

    /**
     * Sets the found value to true.
     */
    fun lyricFound() {
        found = true
    }
}