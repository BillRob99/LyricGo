package com.example.lyricgo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * This class is used to insert a list of songs into a RecyclerView.
 *
 * @author Billy Roberts
 * @version 1.0
 *
 * @param songList A list of songs represented as strings to be inserted.
 */
class SongAdapter(private val songList: MutableList<String>): RecyclerView.Adapter<SongAdapter.ViewHolder>(){

    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var txtSong: TextView

        init {
            txtSong = layout.findViewById<View>(R.id.songTxt) as TextView
        }
    }

    /**
     * Inflates song_row_layout.xml into the RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.song_row_layout, parent, false)

        return ViewHolder(v)
    }

    /**
     * Sets the values in the list to the TextView in the RecyclerView.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = songList[position]
        holder.txtSong.setText(info).toString()

    }


    /**
     * Gets the number of items in the list.
     *
     * @return The number of items in the list of songs.
     */
    override fun getItemCount(): Int {
        return songList.size
    }
}