package com.example.project001coffeeshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.project001coffeeshop.data.CoffeeCatalog
import com.example.project001coffeeshop.model.*
import com.example.project001coffeeshop.ui.components.*
import com.example.project001coffeeshop.ui.theme.CoffeeColors

@Composable
fun FavoritesScreen(favorites: Set<String>, onOpen: (String) -> Unit, onAdd: (String) -> Unit, onExplore: () -> Unit) {
    val coffees = CoffeeCatalog.coffees.filter { it.id in favorites }
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Column(Modifier.padding(24.dp)) {
            Text("Your favorites", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text("Good taste deserves a little heart.", style = MaterialTheme.typography.bodyMedium, color = CoffeeColors.Muted)
        }
        if (coffees.isEmpty()) EmptyCoffeeState("A collection of your kind", "Tap the heart on a coffee to keep your favorites close.", Glyph.HEART, "Find your favorite", onExplore)
        else LazyVerticalGrid(GridCells.Adaptive(150.dp), contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(coffees, key = { it.id }) { coffee -> CoffeeCard(coffee, { onOpen(coffee.id) }, { onAdd(coffee.id) }) }
        }
    }
}

@Composable
fun UpdatesScreen(order: DemoOrder?, onTrack: () -> Unit, onOffer: () -> Unit, onExplore: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding(), contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item {
            Text("A little coffee news", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(6.dp))
            Text("Your orders, treats, and happy updates.", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
        }
        if (order != null) item {
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White, onClick = onTrack) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CoffeeIcon(if (order.stage == 3) Glyph.CHECK else Glyph.DELIVERY, tint = CoffeeColors.Copper)
                        Text(if (order.stage == 3) "A lovely coffee moment" else "Your coffee is on its way", style = MaterialTheme.typography.titleMedium)
                    }
                    Text("${order.id} · ${order.itemCount} coffees · ${money(order.totalCents)}", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                    Text(if (order.stage == 3) "View your completed demo order" else "Track your demo order →", color = CoffeeColors.Copper, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        item { PromotionCard(onOffer) }
        item {
            Row(Modifier.fillMaxWidth().background(CoffeeColors.Cream, RoundedCornerShape(16.dp)).padding(18.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CoffeeIcon(Glyph.COFFEE, tint = CoffeeColors.Copper)
                Column {
                    Text("Welcome to JavaGem", style = MaterialTheme.typography.titleMedium)
                    Text("Freshly roasted. Thoughtfully poured. Your next favorite cup is waiting.", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        item { PrimaryButton("Explore the menu", Modifier.fillMaxWidth(), onClick = onExplore) }
    }
}
