package com.example.project001coffeeshop.data

import android.content.Context
import android.util.Log
import com.example.project001coffeeshop.model.*
import org.json.JSONArray
import org.json.JSONObject

/** Local demo preferences only; no account or server is involved. */
class CoffeePreferences(context: Context) {
    private val preferences = context.getSharedPreferences("javagem_frontend", Context.MODE_PRIVATE)
    fun load(): CoffeeState = try {
        val raw = preferences.getString("state", null)
        if (raw == null) CoffeeState() else {
            val json = JSONObject(raw)
            val favorites = json.optJSONArray("favorites") ?: JSONArray()
            val cart = json.optJSONArray("cart") ?: JSONArray()
            val order = json.optJSONObject("order")
            CoffeeState(
                started = json.optBoolean("started"),
                favorites = List(favorites.length()) { favorites.getString(it) }.filter { id -> CoffeeCatalog.coffees.any { it.id == id } }.toSet(),
                cart = List(cart.length()) { index ->
                    val item = cart.getJSONObject(index)
                    CartLine(item.getString("id"), CupSize.valueOf(item.getString("size")),
                        Temperature.valueOf(item.getString("temperature")), item.getInt("quantity").coerceIn(1, 20))
                }.filter { line -> CoffeeCatalog.coffees.any { it.id == line.coffeeId } },
                address = json.optString("address", CoffeeState().address), deliveryNote = json.optString("note"),
                pickup = json.optBoolean("pickup"), offerApplied = json.optBoolean("offer"),
                payment = json.optString("payment", "Cash on delivery"),
                order = order?.let { DemoOrder(it.getString("id"), it.getInt("count"), it.getInt("total"),
                    it.getString("address"), it.getBoolean("pickup"), it.optInt("stage").coerceIn(0, 3)) },
            )
        }
    } catch (failure: Exception) {
        Log.e("JavaGem", "Could not restore local demo state", failure)
        CoffeeState(recoveryMessage = "We couldn't restore your local demo. You can start fresh.")
    }
    fun save(state: CoffeeState) {
        val json = JSONObject().put("started", state.started).put("favorites", JSONArray(state.favorites.toList()))
            .put("address", state.address).put("note", state.deliveryNote).put("pickup", state.pickup)
            .put("offer", state.offerApplied).put("payment", state.payment)
        val cart = JSONArray()
        state.cart.forEach { cart.put(JSONObject().put("id", it.coffeeId).put("size", it.size.name)
            .put("temperature", it.temperature.name).put("quantity", it.quantity)) }
        json.put("cart", cart)
        state.order?.let { json.put("order", JSONObject().put("id", it.id).put("count", it.itemCount)
            .put("total", it.totalCents).put("address", it.address).put("pickup", it.pickup).put("stage", it.stage)) }
        preferences.edit().putString("state", json.toString()).apply()
    }
}
