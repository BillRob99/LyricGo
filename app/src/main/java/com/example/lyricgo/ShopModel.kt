package com.example.lyricgo

/**
 * Class that represents a song pack in the shop.
 *
 * @author Billy Roberts
 * @version 1.0
 * @param name The name of the pack
 * @param cost The cost of the pack
 * @param owned Whether or not the pack is already owned.
 */
class ShopModel(name: String, cost: Int, owned: Boolean) {
    private var name: String
    private var cost: Int
    private var owned: Boolean


    init{
        this.name = name
        this.cost = cost
        this.owned = owned
    }

    fun getName(): String{
        return name
    }

    fun getCost(): Int{
        return cost
    }

    fun getOwned(): Boolean{
        return owned
    }
}