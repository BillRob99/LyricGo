package com.example.lyricgo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

/**
 * This class is used to inset information for the shop into a RecyclerView.
 *
 * @author Billy Roberts
 * @version 1.0
 */
class ShopModelAdapter(private val shopModelArrayList: MutableList<ShopModel>, val context: Context) : RecyclerView.Adapter<ShopModelAdapter.ViewHolder>() {

    /**
     * The class for the holder of the shop row layout.
     */
    class ViewHolder(var layout: View) : RecyclerView.ViewHolder(layout) {
        var buyImg: ImageView
        var txtName: TextView
        var txtCost: TextView


        init {
            buyImg = layout.findViewById<View>(R.id.buyButton) as ImageView
            txtName= layout.findViewById<View>(R.id.packName) as TextView
            txtCost = layout.findViewById<View>(R.id.packCostTxt) as TextView
        }
    }

    /**
     * inflates the shop row layout to the view holder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.shop_row_layout, parent, false)

        return ViewHolder(v)
    }

    /**
     * Binds the information to the components of the view.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = shopModelArrayList[position]

        //Decides whether the image
        if(info.getOwned()){
            holder.buyImg.setImageResource(R.drawable.bought)
        } else {
            holder.buyImg.setImageResource(R.drawable.buy)
        }
        holder.txtName.setText(info.getName())
        holder.txtCost.setText(context.getString(R.string.cost) + info.getCost().toString())

        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.shared_pref), Context.MODE_PRIVATE)

        holder.layout.setOnClickListener {v ->
            val coins = sharedPref.getInt(context.getString(R.string.coins), 0)
            if(info.getOwned()) {
                val snackbar = Snackbar.make(v, context.getString(R.string.already_owned), Snackbar.LENGTH_SHORT)
                snackbar.show()
            } else if(coins < info.getCost()) {
                val snackbar = Snackbar.make(v, context.getText(R.string.not_enough_coins), Snackbar.LENGTH_SHORT)
                snackbar.show()
            } else {
                val builder = AlertDialog.Builder(context)
                builder.setTitle(context.getString(R.string.are_you_sure))
                builder.setMessage(context.getString(R.string.sure_message) + info.getName())
                builder.setNegativeButton(context.getString(R.string.no)){ dialog, which ->
                }
                builder.setPositiveButton(context.getString(R.string.yes)){ dialog, which ->
                    val editor = sharedPref.edit()
                    editor .putBoolean(info.getName(), true)
                    editor.putInt(context.getString(R.string.coins), coins - info.getCost())
                    editor.apply()
                    editor.commit()
                    val snackbar = Snackbar.make(v, context.getText(R.string.purchase_made), Snackbar.LENGTH_SHORT)
                    snackbar.show()

                    val intent = Intent(context, Shop::class.java)
                    context.startActivity(intent)
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()

            }
        }

    }


    override fun getItemCount(): Int {
        return shopModelArrayList.size
    }
}