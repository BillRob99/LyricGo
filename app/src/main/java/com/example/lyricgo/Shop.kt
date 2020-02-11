package com.example.lyricgo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * This class is the activity where the user can purchase new song packs.
 *
 * @author Billy Roberts
 * @version 1.0
 */
class Shop : AppCompatActivity() {
    val packCost = 50
    private lateinit var sharedPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        //Creates the toolbar
        val toolBar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        toolBar.setTitle(getString(R.string.app_name))
        toolBar.setSubtitle(getString(R.string.shop))
        setSupportActionBar(toolBar)

        sharedPref = this.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE)

        //Creates the RecyclerView
        val shopModelList = createShopList()
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerview.layoutManager = layoutManager
        val mAdapter = ShopModelAdapter(shopModelList, this)
        recyclerview.adapter = mAdapter

        displayCoins()

    }

    /**
     * Function for creating a list of modes to be added to the RecyclerView
     *
     * @return ArrayList of strings representing modes.
     */
    fun createShopList(): ArrayList<ShopModel>{
        val shopModelList = ArrayList<ShopModel>()
        val modeList = arrayListOf<String>(getString(R.string.country),
            getString(R.string.films), getString(R.string.hip_hop),
            getString(R.string.rock), getString(R.string.theatre))

        for(mode in modeList) {
            val owned = sharedPref.getBoolean(mode, false)
            val shopModel = ShopModel(mode, packCost, owned)
            shopModelList.add(shopModel)

        }

        return shopModelList
    }

    /**
     * Sets coinsTxt to the users value of coins.
     */
    fun displayCoins() {
        val coins = sharedPref.getInt(getString(R.string.coins), 0)
        val coinsTxt = findViewById<TextView>(R.id.coinsTxt)
        coinsTxt.setText(getString(R.string.coins_title) + coins.toString())
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
     * Handles when a user presses an item in the toolbar.
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
