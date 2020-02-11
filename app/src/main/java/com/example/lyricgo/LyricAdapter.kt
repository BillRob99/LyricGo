package com.example.lyricgo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * This class is used to insert a list of Lyrics into a RecyclerView.
 *
 * @author Billy Roberts
 * @version 1.0
 *
 * @param lyricList A list of lyrics to be inserted.
 */
class LyricAdapter(private val lyricList: MutableList<Lyric>): RecyclerView.Adapter<LyricAdapter.ViewHolder>(){

    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var txtLyric: TextView

        init {
            txtLyric = layout.findViewById<View>(R.id.lyricTxt) as TextView
        }
    }

    /**
     * Inflates lyric_row_layout.xml into the RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.lyric_row_layout, parent, false)

        return ViewHolder(v)
    }

    /**
     * Sets the values in the list to the TextView in the RecyclerView.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = lyricList[position]
        holder.txtLyric.setText(info.getWords())

    }


    /**
     * Gets the number of items in the list.
     *
     * @return The number of items in the list of lyrics.
     */
    override fun getItemCount(): Int {
        return lyricList.size
    }
}