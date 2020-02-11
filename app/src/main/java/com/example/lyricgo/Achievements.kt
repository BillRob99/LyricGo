package com.example.lyricgo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Activity where the acievements that the user has collected will be displayed.
 *
 * @author Billy Roberts
 * @version 1.0
 */
class Achievements : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

        //Assign the toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setTitle(getString(R.string.app_name))
        toolbar.setSubtitle(getString(R.string.achievements))
        setSupportActionBar(toolbar)

        //Create the RecyclerView and populate it.
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.layoutManager = layoutManager
        val achievementList = readAchievements()
        val mAdapter = AchievementsAdapter(achievementList)
        recyclerview.adapter = mAdapter
    }

    /**
     * Function used to retreive a list of achievements from the achievments file.
     *
     * @return A list of achievements as strings.
     */
    fun readAchievements(): MutableList<String> {
        val achievementList = ArrayList<String>()
        val a = MapsActivity()
        if(fileExists(getString(R.string.achievements_filename))){
            try {
                val fileInputStream = openFileInput(getString(R.string.achievements_filename))
                val inputStreamReader = InputStreamReader(fileInputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                bufferedReader.forEachLine {
                    achievementList.add(it)
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            return achievementList
        } else {
            return achievementList
        }
    }

    /**
     * File to check whether a file exists.
     *
     * @param filename The filename to check whether exists.
     * @return Boolean value describing whether the file exists.
     */
    fun fileExists(filename: String): Boolean {
        val file = baseContext.getFileStreamPath(filename)
        return file.exists()
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
     * This function handles when an item in the toolbar is clicked.
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
