package com.example.project001coffeeshop

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource

class CoffeeFlowTest {
    @get:Rule(order = 0) val cleanIsolatedDemo = object : ExternalResource() {
        override fun before() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            check(context.packageName.endsWith(".frontendTest")) { "Tests must never reset the normal app's preferences" }
            check(context.getSharedPreferences("javagem_frontend", 0).edit().clear().commit())
        }
    }
    @get:Rule(order = 1) val compose = createAndroidComposeRule<MainActivity>()

    private fun start() { compose.onNodeWithText("Get Started").performClick() }

    @Test fun searchAndCategoryFiltersWork() {
        start()
        compose.onNodeWithText("Search coffee").performTextInput("not-a-coffee")
        compose.onNodeWithText("No coffee found").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Reset search").performClick()
        compose.onNodeWithText("Caffe Mocha").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Category Macchiato").performScrollTo().performClick()
        compose.onNodeWithText("1 coffee").assertExists()
    }

    @Test fun detailFavoriteAndBagSurviveActivityRecreation() {
        start()
        compose.onNodeWithText("Caffe Mocha").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Add to favorites").performClick()
        compose.onNodeWithContentDescription("Size L").performScrollTo().performClick()
        compose.onNodeWithText("Iced").performScrollTo().performClick()
        compose.onNodeWithText("Buy now").performClick()
        compose.onNodeWithText("L · Iced").assertExists()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("L · Iced").assertExists()
        compose.onNodeWithContentDescription("Back").performClick()
        compose.onNodeWithContentDescription("Favorites").performClick()
        compose.onNodeWithText("Caffe Mocha").assertIsDisplayed()
    }

    @Test fun checkoutOfferAndSimulatedTrackingWork() {
        start()
        compose.onNodeWithContentDescription("Add Caffe Mocha").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Add Flat White").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Bag").performClick()
        compose.onNodeWithText("Buy one, get one free").performScrollTo().performClick()
        compose.onNodeWithText("JAVAGEM savings").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Place demo order").performClick()
        compose.onNodeWithText("Order received").performScrollTo().assertIsDisplayed()
        repeat(3) { compose.onNodeWithText("Advance demo delivery").performScrollTo().performClick() }
        compose.onNodeWithText("Delivered. Enjoy!").assertExists()
    }

    @Test fun favoritesAndBagCanBeEmptyAndLastItemCanBeRemoved() {
        start()
        compose.onNodeWithContentDescription("Favorites").performClick()
        compose.onNodeWithText("A collection of your kind").assertIsDisplayed()
        compose.onNodeWithContentDescription("Home").performClick()
        compose.onNodeWithContentDescription("Add Caffe Mocha").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Bag").performClick()
        compose.onNodeWithContentDescription("Decrease Caffe Mocha").performClick()
        compose.onNodeWithText("Your bag is taking a break").assertIsDisplayed()
    }
}
