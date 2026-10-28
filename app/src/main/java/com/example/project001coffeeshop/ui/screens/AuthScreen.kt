package com.example.project001coffeeshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project001coffeeshop.R
import com.example.project001coffeeshop.model.*
import com.example.project001coffeeshop.ui.components.*
import com.example.project001coffeeshop.ui.theme.CoffeeColors

@Composable
fun AuthScreen(state: AuthUiState, onBack: () -> Unit, onPage: (AuthPage) -> Unit, onName: (String) -> Unit,
    onEmail: (String) -> Unit, onPassword: (String) -> Unit, onConfirm: (String) -> Unit,
    onSubmit: () -> Unit) {
    val focus = LocalFocusManager.current
    val signup = state.page == AuthPage.SIGN_UP
    val reset = state.page == AuthPage.RESET_PASSWORD
    var passwordVisible by remember(state.page) { mutableStateOf(false) }
    var confirmVisible by remember(state.page) { mutableStateOf(false) }
    fun submit() { focus.clearFocus(); onSubmit() }

    Column(Modifier.fillMaxSize().navigationBarsPadding().imePadding()) {
        if (state.page == AuthPage.SIGN_IN) Box(Modifier.fillMaxWidth().statusBarsPadding().height(60.dp), contentAlignment = Alignment.Center) {
            Text("Your coffee corner", style = MaterialTheme.typography.titleMedium)
        } else ScreenHeader(if (reset || state.page == AuthPage.RESET_PREVIEW) "Password help" else "Create account", onBack)
        if (state.page == AuthPage.RESET_PREVIEW) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Spacer(Modifier.height(24.dp))
                Box(Modifier.size(96.dp).background(CoffeeColors.Cream, CircleShape), contentAlignment = Alignment.Center) {
                    CoffeeIcon(Glyph.NOTE, Modifier.size(40.dp), CoffeeColors.Copper)
                }
                Text("Reset preview ready", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Text(state.email.trim(), color = CoffeeColors.Copper, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                Text("No email was sent. This is a frontend preview. Password recovery will work after an authentication service is connected.",
                    color = CoffeeColors.Muted, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
                PrimaryButton("Back to sign in", Modifier.fillMaxWidth()) { onPage(AuthPage.SIGN_IN) }
            }
        } else Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            AuthBanner()
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(when { signup -> "Good coffee. Great company."; reset -> "Forgot your password?"; else -> "Welcome back." }, style = MaterialTheme.typography.headlineMedium)
                Text(when { signup -> "Make yourself at home in your coffee corner."; reset -> "Enter your email to explore the recovery flow."; else -> "Your favorite cup is just a moment away." },
                    style = MaterialTheme.typography.bodyMedium, color = CoffeeColors.Muted)
            }
            Surface(color = CoffeeColors.Cream, shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CoffeeIcon(Glyph.USER, Modifier.size(18.dp), CoffeeColors.Copper)
                    Text("Frontend demo · Credentials are not verified or saved.", style = MaterialTheme.typography.bodySmall, color = CoffeeColors.Ink)
                }
            }
            if (signup) AuthField(state.name, onName, "Full name", "auth-name", state.errors.name,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
            AuthField(state.email, onEmail, "Email address", "auth-email", state.errors.email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = if (reset) ImeAction.Done else ImeAction.Next),
                onDone = { if (reset) submit() })
            if (!reset) {
                AuthField(state.password, onPassword, "Password", "auth-password", state.errors.password,
                    password = true, visible = passwordVisible, onVisibility = { passwordVisible = !passwordVisible },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = if (signup) ImeAction.Next else ImeAction.Done),
                    onDone = { if (!signup) submit() })
                if (signup) AuthField(state.confirm, onConfirm, "Confirm password", "auth-confirm", state.errors.confirm,
                    password = true, visible = confirmVisible, onVisibility = { confirmVisible = !confirmVisible },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done), onDone = ::submit)
                else Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton({ focus.clearFocus(); onPage(AuthPage.RESET_PASSWORD) }, contentPadding = PaddingValues(horizontal = 0.dp)) { Text("Forgot password?") }
                }
            }
            PrimaryButton(when { signup -> "Create demo profile"; reset -> "Preview reset"; else -> "Preview sign in" }, Modifier.fillMaxWidth(), onClick = ::submit)
            if (!reset) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (signup) "Already have an account?" else "New to JavaGem?", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                    TextButton({ focus.clearFocus(); onPage(if (signup) AuthPage.SIGN_IN else AuthPage.SIGN_UP) }) {
                        Text(if (signup) "Sign in" else "Create account")
                    }
                }
            }
            Text(if (reset) "No email is sent in this demo." else "Use sample details only. No real account is created. Your demo profile lasts for this app session.",
                color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun AuthBanner() {
    Box(Modifier.fillMaxWidth().height(144.dp).clip(RoundedCornerShape(18.dp))) {
        Image(painterResource(R.drawable.coffee_promo), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFF38271E).copy(alpha = .8f), Color.Transparent))))
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Brand(light = true)
            Text("Your daily ritual,\none tap away.", color = Color.White, fontSize = 21.sp, lineHeight = 26.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AuthField(value: String, onChange: (String) -> Unit, label: String, tag: String, error: String?,
    password: Boolean = false, visible: Boolean = false, onVisibility: () -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default, onDone: () -> Unit = {}) {
    OutlinedTextField(value, onChange, Modifier.fillMaxWidth().testTag(tag), label = { Text(label) }, singleLine = true,
        shape = RoundedCornerShape(14.dp), isError = error != null,
        supportingText = if (error != null) ({ Text(error) }) else null,
        visualTransformation = if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (password) ({ TextButton(onVisibility) { Text(if (visible) "Hide" else "Show") } }) else null,
        keyboardOptions = keyboardOptions, keyboardActions = KeyboardActions(onDone = { onDone() }),
        colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White,
            unfocusedBorderColor = CoffeeColors.Border, focusedBorderColor = CoffeeColors.Copper))
}
