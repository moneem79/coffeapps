package com.example.project001coffeeshop

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource

class AuthFlowTest {
    @get:Rule(order = 0) val isolatedReset = object : ExternalResource() {
        override fun before() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            check(context.packageName.endsWith(".frontendTest"))
            check(context.getSharedPreferences("javagem_frontend", 0).edit().clear().putString("state", "{\"started\":true}").commit())
        }
    }
    @get:Rule(order = 1) val compose = createAndroidComposeRule<MainActivity>()
    private fun open() {
        compose.onNodeWithText("Welcome back.").assertIsDisplayed()
        compose.onNodeWithText("Get Started").assertDoesNotExist()
        compose.onNodeWithText("Continue as guest").assertDoesNotExist()
        compose.onNodeWithText("Made for your moment").assertDoesNotExist()
    }

    @Test fun invalidSignInCannotOpenMenuButValidDemoInputCan() {
        open()
        compose.onNodeWithText("Preview sign in").performScrollTo().performClick()
        compose.onNodeWithText("Enter a valid email address.").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Use at least 8 characters.").assertExists()
        compose.onNodeWithText("Made for your moment").assertDoesNotExist()
        compose.onNodeWithTag("auth-email").performScrollTo().performTextInput("coffee@example.com")
        compose.onNodeWithTag("auth-password").performScrollTo().performTextInput("sample123")
        compose.onNodeWithText("Preview sign in").performScrollTo().performClick()
        compose.onNodeWithText("Made for your moment").assertExists()
    }
    @Test fun signupOpensMenuAndSignOutReturnsToSignIn() {
        open()
        compose.onNodeWithText("Create account").performScrollTo().performClick()
        compose.onNodeWithTag("auth-name").performScrollTo().performTextInput("Coffee Lover")
        compose.onNodeWithTag("auth-email").performScrollTo().performTextInput("coffee@example.com")
        compose.onNodeWithTag("auth-password").performScrollTo().performTextInput("sample123")
        compose.onNodeWithTag("auth-confirm").performScrollTo().performTextInput("sample123")
        compose.onNodeWithText("Create demo profile").performScrollTo().performClick()
        compose.onNodeWithContentDescription("Your coffee profile").performClick()
        compose.onNodeWithText("Coffee Lover").assertExists()
        compose.onNodeWithText("Demo profile · not authenticated").assertExists()
        compose.onNodeWithText("Sign out of demo").performClick()
        compose.onNodeWithText("Welcome back.").assertIsDisplayed()
        compose.onNodeWithText("Made for your moment").assertDoesNotExist()
    }
    @Test fun resetExplainsThatNoEmailWasSent() {
        open()
        compose.onNodeWithText("Forgot password?").performScrollTo().performClick()
        compose.onNodeWithTag("auth-email").performTextInput("coffee@example.com")
        compose.onNodeWithText("Preview reset").performScrollTo().performClick()
        compose.onNodeWithText("Reset preview ready").assertIsDisplayed()
        compose.onNodeWithText("No email was sent.", substring = true).assertExists()
        compose.onNodeWithText("Back to sign in").performClick()
        compose.onNodeWithText("Welcome back.").assertExists()
    }
}
