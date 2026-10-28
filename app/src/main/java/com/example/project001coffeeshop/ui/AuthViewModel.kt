package com.example.project001coffeeshop.ui

import androidx.lifecycle.ViewModel
import com.example.project001coffeeshop.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Presentation-only account preview. This does not authenticate or authorize a user. */
class AuthViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(AuthUiState())
    val state = mutableState.asStateFlow()

    fun open(page: AuthPage = AuthPage.SIGN_IN) {
        mutableState.value = AuthUiState(page = page, email = state.value.email, profile = state.value.profile)
    }
    fun name(value: String) { mutableState.value = state.value.copy(name = value.take(80), errors = state.value.errors.copy(name = null)) }
    fun email(value: String) { mutableState.value = state.value.copy(email = value.take(254), errors = state.value.errors.copy(email = null)) }
    fun password(value: String) { mutableState.value = state.value.copy(password = value.take(128), errors = state.value.errors.copy(password = null, confirm = null)) }
    fun confirm(value: String) { mutableState.value = state.value.copy(confirm = value.take(128), errors = state.value.errors.copy(confirm = null)) }

    /** Returns true only to open the menu with an explicitly unverified demo profile. */
    fun submit(): Boolean {
        val current = state.value
        val errors = AuthValidation.errors(current)
        if (errors.any) { mutableState.value = current.copy(errors = errors); return false }
        if (current.page == AuthPage.RESET_PASSWORD) {
            mutableState.value = current.copy(page = AuthPage.RESET_PREVIEW, password = "", confirm = "", errors = AuthErrors())
            return false
        }
        if (current.page !in listOf(AuthPage.SIGN_IN, AuthPage.SIGN_UP)) return false
        val email = current.email.trim()
        val displayName = if (current.page == AuthPage.SIGN_UP) current.name.trim() else email.substringBefore('@')
        mutableState.value = AuthUiState(profile = DemoProfile(displayName, email))
        return true
    }

    fun clearForm() { mutableState.value = AuthUiState(profile = state.value.profile) }
    fun signOut() { mutableState.value = AuthUiState() }
}
