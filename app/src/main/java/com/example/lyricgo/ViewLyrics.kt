package com.example.lyricgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_view_lyrics.*
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

/**
 * This class is used for the activity where the user can view lyrics and guess the song name.
 *
 * @author Billy Roberts
 * @version 1.0
 */
class ViewLyrics : AppCompatActivity() {
    private lateinit var currentSong : Song
    private var chancesLeft = 3
    private lateinit var sharedPref : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_lyrics)
        //Adds the toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolbar.setTitle(getString(R.string.app_name))
        toolbar.setSubtitle(getString(R.string.lyrics))
        setSupportActionBar(toolbar)
        val extras = intent.extras
        currentSong = extras!!.get(getString(R.string.current_song)) as Song
        val lyrics = extras.get(getString(R.string.lyrics)) as HashMap<String, Lyric>
        sharedPref = this.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)
        chancesLeft = sharedPref.getInt(getString(R.string.guesses_left), 3)


        val txtChances = findViewById<TextView>(R.id.chancesTxt)
        txtChances.setText(chancesLeft.toString())

        val lyricsFound = convertToArrayList(lyrics)

        //Adds and inflates information into the RecyclerView.
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.layoutManager = layoutManager
        val mAdapter = LyricAdapter(lyricsFound)
        recyclerview.adapter = mAdapter
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
     * Used to convert a HashMap of Lyrics into an ArrayList of Lyrics that have been found.
     *
     * @param hashMap Lyrics that need to be converted.
     * @return ArrayList of Lyrics that have been found by the user.
     */
    private fun convertToArrayList(hashMap: HashMap<String, Lyric>) : ArrayList<Lyric> {
        val list = ArrayList<Lyric>()
        for(lyric in hashMap){
            if(lyric.value.getFound()){
                list.add(lyric.value)
            }
        }
        return list
    }

    /**
     * Used when the user clicks the Guess button.
     */
    fun makeGuess(view: View){
        val txtGuess = findViewById<EditText>(R.id.songGuessText)
        val editor = sharedPref.edit()
        val txtChances = findViewById<TextView>(R.id.chancesTxt)
        if(txtGuess.text.toString().equals(currentSong.getName() + " - " + currentSong.getArtist())){
            chancesLeft = 3
            txtChances.setText(chancesLeft.toString())
            editor.putInt(getString(R.string.guesses_left), chancesLeft)
            editor.apply()
            editor.commit()
            correctGuess()

        } else {
            //When the user guesses incorrectly.

            if(chancesLeft == 1) {
                outOfChances()
            } else {
                val builder = AlertDialog.Builder(this@ViewLyrics)
                builder.setTitle(getString(R.string.incorrect))
                builder.setMessage(getString(R.string.incorrect_guess))

                builder.setPositiveButton(getString(R.string.whoops)) { dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                chancesLeft -= 1
                txtChances.setText(chancesLeft.toString())
                editor.putInt(getString(R.string.guesses_left), chancesLeft)
                editor.apply()
                editor.commit()
            }
        }
    }

    /**
     * Used when a song is guessed correctly. It adds the song found to the song found file,
     * and navigates back to the map activity.
     */
    fun correctGuess(){
        //Creates tools to write to file
        val fileOut = openFileOutput(getString(R.string.songs_found_filename), Context.MODE_APPEND)
        val outputWriter = OutputStreamWriter(fileOut)

        outputWriter.append(currentSong.getName() + " - " + currentSong.getArtist())
        outputWriter.append(System.lineSeparator())
        outputWriter.close()

        val builder = AlertDialog.Builder(this@ViewLyrics)
        builder.setTitle(getString(R.string.congratulations))
        builder.setMessage(getString(R.string.correct_guess))
        addCoins()

        checkAchievement()

        builder.setPositiveButton(getString(R.string.great)){ dialog, which ->
            val intent = Intent(this, MapsActivity::class.java)
            //Indicates to the MapsActivity that the song has been found.
            intent.putExtra(getString(R.string.has_song_been_found), true)
            //Gives information about the current mode.
            intent.putExtra(getString(R.string.mode), currentSong.getSongPack())
            startActivity(intent)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /**
     * Adds 5 to the users current coin total
     */
    fun addCoins(){
        val editor = sharedPref.edit()
        editor.putInt(getString(R.string.coins), sharedPref.getInt(getString(R.string.coins), 0) + 5)
        editor.apply()
        editor.commit()
    }

    /**
     * Called when the user guesses wrong with their last guess.
     * Takes the user back to the MapsActivity, where they will be assigned a new song.
     */
    fun outOfChances(){
        val editor = sharedPref.edit()
        chancesLeft = 3
        editor.putInt(getString(R.string.guesses_left), chancesLeft)
        editor.apply()
        editor.commit()

        val builder = AlertDialog.Builder(this@ViewLyrics)
        builder.setTitle(getString(R.string.incorrect))
        builder.setMessage(getString(R.string.out_of_guesses))

        builder.setPositiveButton(getString(R.string.whoops)) { dialog, which ->
            val intent = Intent(this, MapsActivity::class.java)
            //Indicates to the MapsActivity that a new song is needed.
            intent.putExtra(getString(R.string.has_song_been_found), true)
            //Gives information about the current mode.
            intent.putExtra(getString(R.string.mode), currentSong.getSongPack())
            startActivity(intent)
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /**
     * Checks whether the user has met the requirements for an achievement.
     */
    private fun checkAchievement() {
        val sharedPref = this.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        var score = sharedPref.getInt(getString(R.string.score), 0)
        score += 1
        editor.putInt(getString(R.string.score), score)
        editor.apply()
        editor.commit()

        if(score % 10 == 0) {
            val achievementName = getString(R.string.song_wizz) + (score / 10).toString()
            val fileOut = openFileOutput(getString(R.string.achievements_filename), Context.MODE_APPEND)
            val outputWriter = OutputStreamWriter(fileOut)
            outputWriter.append(achievementName)
            outputWriter.append(System.lineSeparator())
            outputWriter.close()

            val builder = AlertDialog.Builder(this@ViewLyrics)
            builder.setTitle(getString(R.string.achievement_unlocked_title))
            builder.setMessage(getString(R.string.achievement_unlocked) + achievementName)

            builder.setPositiveButton(getString(R.string.great)){ dialog, which ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()

        }
    }

    /**
     * Handles when an item is selected in the toolbar.
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
