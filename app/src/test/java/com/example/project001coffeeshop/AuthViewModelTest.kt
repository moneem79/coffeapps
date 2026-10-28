package com.example.project001coffeeshop

import com.example.project001coffeeshop.model.*
import com.example.project001coffeeshop.ui.AuthViewModel
import org.junit.Assert.*
import org.junit.Test

class AuthViewModelTest {
    @Test fun invalidEmailAndShortPasswordShowErrors() {
        val model = AuthViewModel()
        model.email("not an email"); model.password("123")
        assertFalse(model.submit())
        assertNotNull(model.state.value.errors.email)
        assertNotNull(model.state.value.errors.password)
        assertNull(model.state.value.profile)
    }
    @Test fun signupRequiresNameAndMatchingConfirmation() {
        val state = AuthUiState(page = AuthPage.SIGN_UP, name = " ", email = "coffee@example.com", password = "sample123", confirm = "different")
        val errors = AuthValidation.errors(state)
        assertNotNull(errors.name); assertNotNull(errors.confirm)
        assertNull(errors.email); assertNull(errors.password)
    }
    @Test fun signupCreatesOnlyInMemoryProfileAndClearsCredentials() {
        val model = AuthViewModel()
        model.open(AuthPage.SIGN_UP)
        model.name(" Coffee Lover "); model.email(" coffee@example.com ")
        model.password("sample123"); model.confirm("sample123")
        assertTrue(model.submit())
        assertEquals(DemoProfile("Coffee Lover", "coffee@example.com"), model.state.value.profile)
        assertEquals("", model.state.value.password)
        assertEquals("", model.state.value.confirm)
    }
    @Test fun signInIsClearlyUnverifiedDemoAndLogoutClearsProfile() {
        val model = AuthViewModel()
        model.email("guest@example.com"); model.password("sample123")
        assertTrue(model.submit())
        assertEquals("guest", model.state.value.profile?.name)
        model.signOut()
        assertNull(model.state.value.profile)
        assertEquals("", model.state.value.password)
    }
    @Test fun resetPreviewOnlyRequiresEmailAndDoesNotSignIn() {
        val model = AuthViewModel()
        model.open(AuthPage.RESET_PASSWORD); model.email("coffee@example.com")
        assertFalse(model.submit())
        assertEquals(AuthPage.RESET_PREVIEW, model.state.value.page)
        assertNull(model.state.value.profile)
    }
    @Test fun switchingOrLeavingFormsClearsPasswords() {
        val model = AuthViewModel()
        model.email("coffee@example.com"); model.password("temporary")
        model.open(AuthPage.SIGN_UP)
        assertEquals("coffee@example.com", model.state.value.email)
        assertEquals("", model.state.value.password)
        model.password("temporary"); model.clearForm()
        assertEquals("", model.state.value.email)
        assertEquals("", model.state.value.password)
    }
}
