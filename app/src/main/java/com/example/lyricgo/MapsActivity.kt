package com.example.lyricgo


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.lang.UCharacter.GraphemeClusterBreak.V
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID
import com.google.android.gms.maps.model.*
import java.nio.charset.Charset
import kotlin.random.Random
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import java.io.*
import java.nio.file.Files.exists
import java.nio.file.Files.write


/**
 * This class contains the code to run map, and the game itself.
 * @author William Roberts
 * @version 1.0
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnMarkerClickListener{

    val PERMISSION_ID = 42
    private lateinit var mMap: GoogleMap
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var userLocationMarker: Marker? = null
    private var currentSong: Song? = null
    private val numberOfLyricMarkers = 100
    private var currentMode = "Classic"
    private var lyrics = HashMap<String, Lyric>()
    private val zoomLevel: Float = 17.toFloat()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Provides access to system location services
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val extras = intent.extras

        if(fileExists(currentMode + getString(R.string.song_info_filename))){
            //If the user has used the app before, read their information.
            readSongInfo()
        } else {
            //If it is the first time, assign a random song.
            assignSong()
        }

        if(extras != null) {
            if(extras.containsKey(getString(R.string.mode))){
                currentMode = extras.getString(getString(R.string.mode))!!
                if(fileExists(currentMode + getString(R.string.song_info_filename))){
                    readSongInfo()
                } else {
                    assignSong()
                }
            }
            if(extras.getBoolean(getString(R.string.has_song_been_found))) {
                assignSong()
                writeInfo()
                readLyrics()
            }
        }


        readLyrics()
        readSongInfo()
        writeInfo()
        toolbar.setTitle(getString(R.string.app_name))
        toolbar.setSubtitle(currentMode)

        setSupportActionBar(toolbar)
    }

    /**
     * Inflates the toolbar options.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_toolbar_layout, menu)
        return true
    }

    /**
     * Handles the options in the toolbar being clicked.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sharedPref = this.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(getString(R.string.mode), currentMode)
        editor.apply()
        editor.commit()

        when(item!!.itemId){
            //Navigates to the ViewLyrics activity.
            R.id.viewLyrics->{
                val intent = Intent(this, ViewLyrics::class.java)
                intent.putExtra(getString(R.string.lyrics), lyrics)
                intent.putExtra(getString(R.string.current_song), currentSong)

                startActivity(intent)
                return true
            }
            //Navigates to ViewSongs activity.
            R.id.allSongs->{
                val intent = Intent(this, ViewSongs::class.java)
                startActivity(intent)
                return true
            }
            //Skips the current song.
            R.id.skipSong->{
                assignSong()
                writeInfo()
                readLyrics()
                Toast.makeText(this, getString(R.string.song_skipped), Toast.LENGTH_SHORT).show()
            }
            //Navigates to the ChangeMode activity.
            R.id.changeMode->{
                val intent = Intent(this, ChangeMode::class.java)
                startActivity(intent)
            }
            //Navigates to the Shop activity.
            R.id.shop->{
                val intent = Intent(this, Shop::class.java)
                startActivity(intent)
            }
            //Navigates to the achievements activity.
            R.id.achievements->{
                val intent = Intent(this, Achievements::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        with(mMap) {
            uiSettings.isZoomControlsEnabled = true
            mapType = MAP_TYPE_HYBRID
            setMinZoomPreference(zoomLevel)
            setOnMarkerClickListener(this@MapsActivity)
        }

        //Start tracking users location.
        getLastLocation()
        populateMarkers(numberOfLyricMarkers)
    }


    /**
     * Method to track the users current location whilst using the map.
     */
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                requestNewLocationData()
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    /**
     * Function to request information on the users location.
     */
    private fun requestNewLocationData() {
        Log.i("myLocation", "request")
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 2000
        mLocationRequest.fastestInterval = 1000

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        try{
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper())

        } catch (e:SecurityException){
            Toast.makeText(this, "Error with map, please check permissions."
                , Toast.LENGTH_LONG).show()
        }


    }

    /**
     * Function for tracking the users location.
     */
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.i("myLocation", "Callback")
            var mLastLocation: Location = locationResult.lastLocation
            var lat = mLastLocation.latitude
            var long = mLastLocation.longitude
            val lastLoc = LatLng(lat, long)
            val temp = userLocationMarker

            //Check whether a marker already existing on the map.
            if(temp != null) {
                //If it does, remove it.
                temp.remove()
            }

            //Add the new marker to the map and move camera to position.
            userLocationMarker = mMap.addMarker(MarkerOptions().position(lastLoc).title("You"))
            mMap!!.animateCamera(CameraUpdateFactory.newLatLng(lastLoc))

        }
    }

    /**
     * Checks whether the user has enabled their location services.
     *
     * @return Boolean value indicating whether location is enabled.
     */
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /**
     * Checks whether the user has allowed the app access to location services.
     *
     * @return Boolean value indicating whether the app has been allowed access.
     */
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    /**
     * Requests permission for location services.
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    /**
     * If permissions are okay, retrieves location.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    /**
     * Adds markers to the campus area.
     */
    private fun populateMarkers(number: Int){
        //The four extremes of the campus.
        val northPoint = 51.619420
        val eastPoint = -3.875953
        val southPoint = 51.617554
        val westPoint = -3.885054

        for(i in 1..number){
            //Generates the location of a marker within the campus.
            val lat = Random.nextDouble(southPoint, northPoint)
            val long = Random.nextDouble(westPoint, eastPoint)

            //Adds the lyric markers to the map.
            mMap.addMarker(MarkerOptions()
                .position(LatLng(lat, long))
                .title("Lyric")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)))
        }
    }

    /**
     * This function is for when a random song needs to be assigned.
     */
    private fun assignSong() {
        //Retrieves list of all songs in 'Current' folder.
        val songs = application.assets.list(currentMode)
        val songsSize = songs?.size

        if(songs != null && songsSize != null) {
            //Selects random song.
            val fileName = songs.get(Random.nextInt(0, songsSize - 1))
            //Opens the song selected.
            var textFile = application.assets.open(currentMode + "/" + fileName)
            //Converts from InputStream to a String.
            val lyrics = textFile.convertToString()
            val artistAndNameNotSorted = fileName.split("(", ")")
            val artistAndName = ArrayList<String>()
            for(i in 0..1){
                artistAndName.add(artistAndNameNotSorted[i].replace("_", " "))
            }
            val s = Song(artistAndName.get(1), artistAndName.get(0), currentMode, lyrics, fileName)
            currentSong = s
            writeInfo()
        }
    }

    /**
     * This is used when a song is being assigned from memory.
     *
     * @param songFileName The name of the song to be retrieved.
     */
    private fun assignSong(songFileName: String){
        //Finds the song in assets.
        var textFile = application.assets.open(currentMode + "/" + songFileName)
        val lyrics = textFile.convertToString()
        //Separates the song.
        var artistAndNameNotSorted = songFileName.split("(", ")")
        val artistAndName = ArrayList<String>()
        for(i in 0..1){

            //Replaces '_' with spaces.
            artistAndName.add(artistAndNameNotSorted[i].replace("_", " "))

        }
        val song = Song(artistAndName[1], artistAndName[0], currentMode, lyrics, songFileName)
        currentSong = song
    }

    /**
     * Used to collect the lyrics from the assets file.
     */
    private fun readLyrics() {
        val temp = currentSong
        //Requires check as it is a nullable variable.
        if(temp!= null) {
            val lyricsNotSorted = temp.getLyrics().lines()
            lyrics.clear()
            for(line in lyricsNotSorted){
                //Prevents duplicate lyrics being entered.
                if(!lyrics.containsKey(line)){
                    lyrics.put(line, Lyric(line))
                }
            }
        }


    }

    /**
     * Function to calculate the distance between the user and a lyric.
     */
    private fun calculateDistance(userMarker: Marker, lyricMarker: Marker): Float{

        var results = FloatArray(1)
        Location.distanceBetween(userMarker.position.latitude, userMarker.position.longitude,
            lyricMarker.position.latitude, lyricMarker.position.longitude, results)

        return results[0]
    }

    /**
     * Used when a user clicks a marker.
     */
    override fun onMarkerClick(marker: Marker?): Boolean {
        val temp = userLocationMarker
        val builder = AlertDialog.Builder(this@MapsActivity)

        if(marker != userLocationMarker && temp != null && marker != null) {

            val distance = calculateDistance(temp, marker)
            //If they are close enough to the marker.
            if(distance < 30){
                marker.remove()
                //Builds a pop-up window.
                val builder = AlertDialog.Builder(this@MapsActivity)
                builder.setTitle(getString(R.string.collected_lyric))
                val lyricCollected = collectLyric()


                if(lyricCollected != null){
                    builder.setMessage(lyricCollected.getWords())
                    builder.setPositiveButton(getString(R.string.collect)){ dialog, which ->
                        Toast.makeText(this, getString(R.string.collected_lyric), Toast.LENGTH_SHORT).show()
                        lyrics.put(lyricCollected.getWords(), lyricCollected)
                        writeInfo()
                        populateMarkers(1)
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                }

            } else {
                Toast.makeText(this, getString(R.string.too_far_away), Toast.LENGTH_SHORT).show()
            }
        }
        //Returning false indicates that the regular onMarkerClick should occur as well.
        return false
    }

    /**
     * Writes the users progress in the current mode to a file.
     */
    fun writeInfo(){
        try {
            val fileOut: FileOutputStream
            //Opens the file for the current mode.
            fileOut = openFileOutput(currentMode + getString(R.string.song_info_filename), Context.MODE_PRIVATE)


            val outputWriter = OutputStreamWriter(fileOut)
            val temp = currentSong
            if(temp != null){
                outputWriter.write(temp.getFilename())
                outputWriter.append(System.lineSeparator())
                for(l in lyrics){
                    if(l.value.getFound()){
                        outputWriter.append(l.value.getWords())
                        outputWriter.append(System.lineSeparator())
                    }
                }
            }
            outputWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Function to read the current song from internal storage.
     */
    private fun readSongInfo(){
        try {

            var fileInputStream = openFileInput(currentMode + getString(R.string.song_info_filename))
            var inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var songFileName = bufferedReader.readLine()

            assignSong(songFileName)

            bufferedReader.forEachLine {
                if(lyrics.containsKey(it)){
                    val l = Lyric(it)
                    l.lyricFound()
                    lyrics.put(it, l)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * Function for deciding which lyric will be collected.
     *
     * @return The lyric to be collected, or null if all lyrics have been collected.
     */
    private fun collectLyric(): Lyric? {
        val nonFoundLyrics = ArrayList<Lyric>()
        for (lyric in lyrics){
            if(!lyric.value.getFound()){
                nonFoundLyrics.add(lyric.value)
            }
        }
        if(nonFoundLyrics.size > 0){
            val index = Random.nextInt(0, nonFoundLyrics.size - 1)
            nonFoundLyrics[index].lyricFound()

            checkAchievement()

            return nonFoundLyrics[index]
        } else {
            return null
        }
    }

    /**
     * Returns whether or not a file exists.
     * Used to figure out whether it is the users first time using the app.
     *
     * @param fileName The name of the file that will be searched for.
     * @return Boolean value indicating whether the file exists.
     */
     fun fileExists(filename: String): Boolean {
        val file = baseContext.getFileStreamPath(filename)
        return file.exists()
    }

    /**
     * Checks whether the user has met the requirements for an achievement.
     * If they have, gives a pop-up window and writes the achievement to a file.
     */
    private fun checkAchievement() {
        val sharedPref = this.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        var lyricScore = sharedPref.getInt(getString(R.string.lyric_score), 0)
        lyricScore += 1
        editor.putInt(getString(R.string.lyric_score), lyricScore)
        editor.apply()
        editor.commit()

        if(lyricScore % 10 == 0) {
            val achievementName = getString(R.string.lyric_wizz) + (lyricScore / 10).toString()
            val fileOut = openFileOutput(getString(R.string.achievements_filename), Context.MODE_APPEND)
            val outputWriter = OutputStreamWriter(fileOut)
            outputWriter.append(achievementName)
            outputWriter.append(System.lineSeparator())
            outputWriter.close()

            val builder = AlertDialog.Builder(this@MapsActivity)
            builder.setTitle(getString(R.string.achievement_unlocked_title))
            builder.setMessage(getString(R.string.achievement_unlocked) + achievementName)

            builder.setPositiveButton(getString(R.string.great)){ dialog, which ->

            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    /**
     * An extension function for InputStream class.
     * Makes converting into a String much easier.
     */
    private fun InputStream.convertToString(charset: Charset = Charsets.UTF_8): String {
        return this.bufferedReader(charset).use { it.readText() }
    }
}
