package com.example.project001coffeeshop.model

import java.util.Locale

data class Coffee(val id: String, val name: String, val subtitle: String, val category: String,
    val priceCents: Int, val image: Int, val rating: String, val description: String)
enum class CupSize(val label: String, val adjustmentCents: Int) { SMALL("S", -30), MEDIUM("M", 0), LARGE("L", 70) }
enum class Temperature(val label: String) { HOT("Hot"), ICED("Iced") }
enum class CoffeeSort { RECOMMENDED, PRICE_LOW, PRICE_HIGH }
data class CartLine(val coffeeId: String, val size: CupSize, val temperature: Temperature, val quantity: Int) {
    val key: String get() = "$coffeeId:${size.name}:${temperature.name}"
}
data class DemoOrder(val id: String, val itemCount: Int, val totalCents: Int, val address: String,
    val pickup: Boolean, val stage: Int = 0)
data class CoffeeState(
    val started: Boolean = false, val favorites: Set<String> = emptySet(), val cart: List<CartLine> = emptyList(),
    val address: String = "House 12, Road 11, Banani, Dhaka", val deliveryNote: String = "",
    val pickup: Boolean = false, val offerApplied: Boolean = false, val payment: String = "Cash on delivery",
    val order: DemoOrder? = null, val recoveryMessage: String? = null,
)
data class PriceSummary(val subtotal: Int, val discount: Int, val delivery: Int) {
    val total: Int get() = subtotal - discount + delivery
}
fun money(cents: Int): String = String.format(Locale.US, "$ %.2f", cents / 100.0)
object CoffeePricing {
    fun unitPrice(coffee: Coffee, size: CupSize): Int = coffee.priceCents + size.adjustmentCents
    fun summarize(cart: List<CartLine>, catalog: List<Coffee>, offerApplied: Boolean, pickup: Boolean): PriceSummary {
        val prices = cart.flatMap { line ->
            val coffee = catalog.firstOrNull { it.id == line.coffeeId } ?: return@flatMap emptyList()
            List(line.quantity.coerceIn(0, 20)) { unitPrice(coffee, line.size) }
        }.sortedDescending()
        // The lower-priced drink in each pair is complimentary.
        val discount = if (offerApplied) prices.filterIndexed { index, _ -> index % 2 == 1 }.sum() else 0
        return PriceSummary(prices.sum(), discount, if (pickup || prices.isEmpty()) 0 else 100)
    }
}
