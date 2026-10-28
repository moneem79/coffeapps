package com.example.project001coffeeshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.project001coffeeshop.R
import com.example.project001coffeeshop.ui.components.Brand
import com.example.project001coffeeshop.ui.components.PrimaryButton

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFF050505)).navigationBarsPadding()) {
        Box(Modifier.fillMaxWidth().weight(1f)) {
            Image(painterResource(R.drawable.coffee_hero), "Fresh espresso with coffee beans", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(0f to Color.Black.copy(alpha = .2f), .6f to Color.Transparent, 1f to Color(0xFF050505))))
            Brand(Modifier.statusBarsPadding().padding(24.dp), light = true)
        }
        Column(Modifier.fillMaxWidth().padding(horizontal = 30.dp).padding(bottom = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Fall in Love with\nCoffee in Blissful\nDelight!", color = Color.White, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
            Spacer(Modifier.height(14.dp))
            Text("Welcome to our cozy coffee corner, where\nevery cup is a delight for you.", color = Color(0xFFA2A2A2), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(26.dp))
            PrimaryButton("Get Started", Modifier.fillMaxWidth(), onClick = onStart)
        }
    }
}
