package com.example.lyricgo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


/**
 * This class is used to insert a list of modes into a RecyclerView.
 *
 * @author Billy Roberts
 * @version 1.0
 *
 * @param modeList A list of modes represented as Strings to be inserted.
 */
class ModeAdapter(private val modeList: ArrayList<String>, val context: Context): RecyclerView.Adapter<ModeAdapter.ViewHolder>(){

    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var txtMode: TextView

        init {
            txtMode = layout.findViewById<View>(R.id.modeTxt) as TextView
        }
    }

    /**
     * Inflates mode_row_layout.xml into the RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.mode_row_layout, parent, false)

        return ViewHolder(v)
    }

    /**
     * Sets the values in the list to the TextView in the RecyclerView, and deals with them being clicked.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = modeList[position]
        holder.txtMode.setText(info)

        holder.layout.setOnClickListener {v ->
            val snackbar = Snackbar.make(v, "Mode changed to:  $info", Snackbar.LENGTH_LONG)
            snackbar.show()
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra("Mode", info)

            context.startActivity(intent)
        }

    }


    /**
     * Gets the number of items in the list.
     *
     * @return The number of items in the list of modes.
     */
    override fun getItemCount(): Int {
        return modeList.size
    }
}