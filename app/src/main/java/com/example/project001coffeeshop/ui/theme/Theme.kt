package com.example.project001coffeeshop.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object CoffeeColors {
    val Copper = Color(0xFFC67C4E)
    val Ink = Color(0xFF242424)
    val Muted = Color(0xFF909090)
    val Background = Color(0xFFF9F9F9)
    val Cream = Color(0xFFF9F2ED)
    val Border = Color(0xFFEAEAEA)
    val Green = Color(0xFF3D8168)
}
private val CoffeeScheme = lightColorScheme(primary = CoffeeColors.Copper, onPrimary = Color.White,
    secondary = CoffeeColors.Ink, background = CoffeeColors.Background, surface = Color.White,
    onSurface = CoffeeColors.Ink, onBackground = CoffeeColors.Ink, surfaceVariant = CoffeeColors.Cream, outline = CoffeeColors.Border)
@Composable
fun Project001coffeeshopTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = CoffeeScheme, typography = Typography, content = content)
}
