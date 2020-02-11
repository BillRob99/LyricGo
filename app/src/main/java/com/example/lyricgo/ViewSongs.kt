package com.example.lyricgo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.file.Files.lines

/**
 * This class is used for the activity where users can view a list of songs they have found.
 */
class ViewSongs : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_songs)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setTitle(getString(R.string.app_name))
        toolbar.setSubtitle(getString(R.string.songs_found_title))
        setSupportActionBar(toolbar)

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.layoutManager = layoutManager
        val songList = readSongs()
        val mAdapter = SongAdapter(songList)
        recyclerview.adapter = mAdapter

        val txtScore = findViewById<TextView>(R.id.scoreTxt)
        txtScore.setText(songList.size.toString())
    }

    /**
     * Inflates the toolbar.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.secondary_toolbar_layout, menu)
        return true
    }

    /**
     * Reads the songs that the user has found from a file.
     */
    fun readSongs(): MutableList<String> {
        val songList = ArrayList<String>()
        val a = MapsActivity()
        if(fileExists(getString(R.string.songs_found_filename))){
            try {
                val fileInputStream = openFileInput(getString(R.string.songs_found_filename))
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                bufferedReader.forEachLine {
                    songList.add(it)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return songList
        } else {
            return songList
        }
    }

    /**
     * Checks whether a given file exists.
     *
     * @return Boolean value describing whether file exists.
     */
    fun fileExists(filename: String): Boolean {
        val file = baseContext.getFileStreamPath(filename)
        return file.exists()
    }

    /**
     * Handles when an item in the toolbar is clicked.
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