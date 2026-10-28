package com.example.project001coffeeshop

import com.example.project001coffeeshop.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CoffeePricingTest {
    private val mocha = Coffee("mocha", "Mocha", "", "Latte", 453, 0, "4.8", "")
    private val flat = Coffee("flat", "Flat", "", "Latte", 353, 0, "4.8", "")
    private val catalog = listOf(mocha, flat)
    @Test fun sizeAdjustmentsAreExactIntegerCents() {
        assertEquals(423, CoffeePricing.unitPrice(mocha, CupSize.SMALL))
        assertEquals(453, CoffeePricing.unitPrice(mocha, CupSize.MEDIUM))
        assertEquals(523, CoffeePricing.unitPrice(mocha, CupSize.LARGE))
    }
    @Test fun emptyBagDoesNotChargeDelivery() {
        assertEquals(PriceSummary(0, 0, 0), CoffeePricing.summarize(emptyList(), catalog, false, false))
    }
    @Test fun totalIncludesSizeQuantityAndDelivery() {
        val cart = listOf(CartLine("mocha", CupSize.MEDIUM, Temperature.HOT, 2), CartLine("flat", CupSize.LARGE, Temperature.ICED, 1))
        assertEquals(1429, CoffeePricing.summarize(cart, catalog, false, false).total)
    }
    @Test fun pickupHasNoDeliveryCharge() {
        val cart = listOf(CartLine("flat", CupSize.MEDIUM, Temperature.HOT, 1))
        assertEquals(353, CoffeePricing.summarize(cart, catalog, false, true).total)
    }
    @Test fun offerMakesLowerPricedCoffeeInPairFree() {
        val cart = listOf(CartLine("mocha", CupSize.MEDIUM, Temperature.HOT, 1), CartLine("flat", CupSize.MEDIUM, Temperature.HOT, 1))
        val price = CoffeePricing.summarize(cart, catalog, true, false)
        assertEquals(353, price.discount)
        assertEquals(553, price.total)
    }
    @Test fun oddCoffeeDoesNotBecomeFreeAndOfferRepeatsPerPair() {
        val cart = listOf(CartLine("mocha", CupSize.MEDIUM, Temperature.HOT, 5))
        assertEquals(906, CoffeePricing.summarize(cart, catalog, true, true).discount)
        assertEquals(1359, CoffeePricing.summarize(cart, catalog, true, true).total)
    }
    @Test fun unknownItemsCannotChargeMoney() {
        val cart = listOf(CartLine("missing", CupSize.LARGE, Temperature.HOT, 1))
        assertEquals(0, CoffeePricing.summarize(cart, catalog, true, false).total)
    }
}
