package com.example.project001coffeeshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project001coffeeshop.model.*
import com.example.project001coffeeshop.ui.components.*
import com.example.project001coffeeshop.ui.theme.CoffeeColors

@Composable
fun DetailScreen(coffee: Coffee, favorite: Boolean, onBack: () -> Unit, onFavorite: () -> Unit,
    onBuy: (CupSize, Temperature) -> Unit) {
    var size by rememberSaveable(coffee.id) { mutableStateOf(CupSize.MEDIUM) }
    var temperature by rememberSaveable(coffee.id) { mutableStateOf(Temperature.HOT) }
    var expanded by rememberSaveable(coffee.id) { mutableStateOf(false) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Detail", onBack) {
            IconButton(onFavorite) { CoffeeIcon(Glyph.HEART, tint = if (favorite) CoffeeColors.Copper else CoffeeColors.Ink,
                description = if (favorite) "Remove from favorites" else "Add to favorites", filled = favorite) }
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            CoffeePhoto(coffee.image, coffee.name, Modifier.fillMaxWidth().height(226.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(coffee.name, style = MaterialTheme.typography.headlineMedium)
                    Text("${temperature.label} · ${coffee.subtitle}", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                }
                listOf(Glyph.DELIVERY to "Freshly prepared", Glyph.BEAN to "Premium roast", Glyph.MILK to "Contains milk options").forEach { (icon, label) ->
                    Box(Modifier.padding(start = 7.dp).size(37.dp).background(CoffeeColors.Cream, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                        CoffeeIcon(icon, Modifier.size(20.dp), CoffeeColors.Copper, label)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                CoffeeIcon(Glyph.STAR, Modifier.size(19.dp), Color(0xFFFBBE21), filled = true)
                Text(coffee.rating, fontWeight = FontWeight.SemiBold)
                Text("(230 reviews)", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
            }
            HorizontalDivider(color = CoffeeColors.Border)
            Column {
                Text("Description", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(coffee.description, color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium,
                    maxLines = if (expanded) Int.MAX_VALUE else 3, overflow = TextOverflow.Ellipsis)
                TextButton({ expanded = !expanded }, contentPadding = PaddingValues(0.dp)) { Text(if (expanded) "Read less" else "Read more") }
            }
            Text("Size", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CupSize.entries.forEach { option ->
                    SelectOption(option.label, size == option, Modifier.weight(1f), "Size ${option.label}") { size = option }
                }
            }
            Text("How do you like it?", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Temperature.entries.forEach { option ->
                    SelectOption(option.label, temperature == option, Modifier.weight(1f)) { temperature = option }
                }
            }
            Text("Freshly made · Prices shown in USD", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
        }
        Surface(color = Color.White, shadowElevation = 10.dp, shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)) {
            Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                Column {
                    Text("Price", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                    Text(money(CoffeePricing.unitPrice(coffee, size)), color = CoffeeColors.Copper, fontSize = 23.sp, fontWeight = FontWeight.SemiBold)
                }
                PrimaryButton("Buy now", Modifier.weight(1f)) { onBuy(size, temperature) }
            }
        }
    }
}

@Composable
fun SelectOption(text: String, isSelected: Boolean, modifier: Modifier = Modifier, description: String = text, onClick: () -> Unit) {
    Box(modifier.heightIn(min = 48.dp).clip(RoundedCornerShape(12.dp))
        .background(if (isSelected) CoffeeColors.Cream else Color.White)
        .border(1.dp, if (isSelected) CoffeeColors.Copper else CoffeeColors.Border, RoundedCornerShape(12.dp))
        .clickable(role = Role.RadioButton, onClick = onClick).semantics { contentDescription = description; selected = isSelected }
        .padding(12.dp), contentAlignment = Alignment.Center) {
        Text(text, color = if (isSelected) CoffeeColors.Copper else CoffeeColors.Ink, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
    }
}
