package com.example.project001coffeeshop.model

enum class AuthPage { SIGN_IN, SIGN_UP, RESET_PASSWORD, RESET_PREVIEW }
data class DemoProfile(val name: String, val email: String)
data class AuthErrors(val name: String? = null, val email: String? = null, val password: String? = null, val confirm: String? = null) {
    val any: Boolean get() = listOf(name, email, password, confirm).any { it != null }
}

/** Passwords exist only in this ViewModel's memory, never preferences, saved state, or logs. */
data class AuthUiState(
    val page: AuthPage = AuthPage.SIGN_IN,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirm: String = "",
    val errors: AuthErrors = AuthErrors(),
    val profile: DemoProfile? = null,
)

object AuthValidation {
    private val emailPattern = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")
    fun errors(state: AuthUiState): AuthErrors = AuthErrors(
        name = if (state.page == AuthPage.SIGN_UP && state.name.trim().length < 2) "Enter your name (at least 2 characters)." else null,
        email = if (!emailPattern.matches(state.email.trim())) "Enter a valid email address." else null,
        password = if (state.page in listOf(AuthPage.SIGN_IN, AuthPage.SIGN_UP) && state.password.length < 8) "Use at least 8 characters." else null,
        confirm = if (state.page == AuthPage.SIGN_UP && state.confirm != state.password) "Passwords don't match." else null,
    )
}
