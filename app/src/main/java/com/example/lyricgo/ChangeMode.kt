package com.example.lyricgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * This Activity is where the user can change the current mode.
 *
 */
class ChangeMode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_mode)
        //Adds the toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setTitle(getString(R.string.app_name))
        toolbar.setSubtitle(getString(R.string.change_mode))
        setSupportActionBar(toolbar)

        //Adds mode information
        val modeList = ArrayList<String>()
        val sharedPref = this.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)
        modeList.add(getString(R.string.classic))
        modeList.add(getString(R.string.current))

        if(sharedPref.getBoolean(getString(R.string.country), false)) {
            modeList.add(getString(R.string.country))
        }
        if(sharedPref.getBoolean(getString(R.string.films), false)) {
            modeList.add(getString(R.string.films))
        }
        if(sharedPref.getBoolean(getString(R.string.hip_hop), false)) {
            modeList.add(getString(R.string.hip_hop))
        }
        if(sharedPref.getBoolean(getString(R.string.rock), false)) {
            modeList.add(getString(R.string.rock))
        }
        if(sharedPref.getBoolean(getString(R.string.theatre), false)) {
            modeList.add(getString(R.string.theatre))
        }

        //Creates and inflates RecyclerView
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.layoutManager = layoutManager
        val mAdapter = ModeAdapter(modeList, this)
        recyclerview.adapter = mAdapter
    }

    /**
     * Inflates the toolbar options.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.secondary_toolbar_layout, menu)
        return true
    }

    /**
     * Handles when options in the toolbar are clicked.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sharedPref = this.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)
        val currentMode = sharedPref.getString(getString(R.string.mode), getString(R.string.classic))

        when(item!!.itemId){
            //Navigates to the ViewLyrics activity.
            R.id.home->{
                val intent = Intent(this, MapsActivity::class.java)
                intent.putExtra(getString(R.string.mode), currentMode)
                startActivity(intent)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }
}
