package com.example.project001coffeeshop.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.project001coffeeshop.data.CoffeeCatalog
import com.example.project001coffeeshop.data.CoffeePreferences
import com.example.project001coffeeshop.model.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class CoffeeViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = CoffeePreferences(application)
    private val mutableState = MutableStateFlow(preferences.load())
    val state = mutableState.asStateFlow()
    private val messages = Channel<String>(Channel.BUFFERED)
    val events = messages.receiveAsFlow()
    private fun change(transform: (CoffeeState) -> CoffeeState) {
        val next = transform(mutableState.value)
        mutableState.value = next
        preferences.save(next)
    }
    fun message(text: String) { messages.trySend(text) }
    fun start() = change { it.copy(started = true, recoveryMessage = null) }
    fun replayWelcome() = change { it.copy(started = false) }
    fun dismissRecovery() = change { it.copy(recoveryMessage = null) }
    fun favorite(id: String) = change { state ->
        state.copy(favorites = if (id in state.favorites) state.favorites - id else state.favorites + id)
    }
    fun addToCart(id: String, size: CupSize = CupSize.MEDIUM, temperature: Temperature = Temperature.HOT) {
        val candidate = CartLine(id, size, temperature, 1)
        val existing = mutableState.value.cart.find { it.key == candidate.key }
        if ((existing?.quantity ?: 0) >= 20) { message("You can add up to 20 of each coffee."); return }
        change { state -> state.copy(cart = if (existing == null) state.cart + candidate
            else state.cart.map { if (it.key == existing.key) it.copy(quantity = it.quantity + 1) else it }) }
        message("${CoffeeCatalog.find(id).name} added to your bag")
    }
    fun quantity(key: String, delta: Int) = change { state ->
        state.copy(cart = state.cart.map { if (it.key == key) it.copy(quantity = (it.quantity + delta).coerceAtMost(20)) else it }.filter { it.quantity > 0 })
    }
    fun pickup(value: Boolean) = change { it.copy(pickup = value) }
    fun address(value: String) {
        if (value.isBlank()) { message("Please enter a delivery address."); return }
        change { it.copy(address = value.trim()) }
    }
    fun note(value: String) = change { it.copy(deliveryNote = value.trim()) }
    fun payment(value: String) = change { it.copy(payment = value) }
    fun applyOffer() {
        change { it.copy(offerApplied = !it.offerApplied) }
        message(if (state.value.offerApplied) "JAVAGEM applied. Add two coffees to enjoy your free drink." else "Offer removed")
    }
    fun placeDemoOrder(): Boolean {
        val current = state.value
        if (current.cart.isEmpty()) { message("Add a coffee to your bag first."); return false }
        val price = CoffeePricing.summarize(current.cart, CoffeeCatalog.coffees, current.offerApplied, current.pickup)
        change { it.copy(order = DemoOrder("JG-${System.currentTimeMillis().toString().takeLast(6)}",
            current.cart.sumOf { line -> line.quantity }, price.total,
            if (current.pickup) "JavaGem Coffee · Banani, Dhaka" else current.address, current.pickup),
            cart = emptyList(), offerApplied = false) }
        return true
    }
    fun advanceOrder() = change { it.copy(order = it.order?.let { order -> order.copy(stage = (order.stage + 1).coerceAtMost(3)) }) }
}
