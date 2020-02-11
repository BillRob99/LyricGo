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
 * @param songList A list of achievements represented as strings to be inserted.
 */
class AchievementsAdapter(private val songList: MutableList<String>): RecyclerView.Adapter<AchievementsAdapter.ViewHolder>(){

    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var txtAchievement: TextView

        init {
            txtAchievement = layout.findViewById<View>(R.id.achievementTxt) as TextView
        }
    }

    /**
     * Inflates achievement_row_layout.xml into the RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.achievement_row_layout, parent, false)

        return ViewHolder(v)
    }

    /**
     * Sets the values in the list to the TextView in the RecyclerView.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = songList[position]
        holder.txtAchievement.setText(info).toString()

    }


    /**
     * Gets the number of items in the list.
     *
     * @return The number of items in the list of achievements.
     */
    override fun getItemCount(): Int {
        return songList.size
    }
}