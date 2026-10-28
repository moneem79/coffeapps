package com.example.project001coffeeshop.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project001coffeeshop.R
import com.example.project001coffeeshop.data.CoffeeCatalog
import com.example.project001coffeeshop.model.CoffeeSort
import com.example.project001coffeeshop.ui.components.*
import com.example.project001coffeeshop.ui.theme.CoffeeColors

@Composable
fun HomeScreen(onCoffee: (String) -> Unit, onAdd: (String) -> Unit, onAddress: () -> Unit,
    onProfile: () -> Unit, onOffer: () -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All Coffee") }
    var sorting by rememberSaveable { mutableStateOf(CoffeeSort.RECOMMENDED) }
    var affordableOnly by rememberSaveable { mutableStateOf(false) }
    var filters by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val matches = CoffeeCatalog.coffees.filter {
        (category == "All Coffee" || it.category == category) &&
            (it.name.contains(query, true) || it.subtitle.contains(query, true)) && (!affordableOnly || it.priceCents < 400)
    }
    val coffees = when (sorting) {
        CoffeeSort.RECOMMENDED -> matches
        CoffeeSort.PRICE_LOW -> matches.sortedBy { it.priceCents }
        CoffeeSort.PRICE_HIGH -> matches.sortedByDescending { it.priceCents }
    }
    LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Box {
                    Box(Modifier.fillMaxWidth().height(248.dp).background(Brush.linearGradient(listOf(Color(0xFF303030), Color(0xFF111111)))))
                    Column(Modifier.statusBarsPadding().padding(horizontal = 24.dp).padding(top = 14.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onAddress).padding(vertical = 4.dp)) {
                                Text("Location", color = Color(0xFFA2A2A2), style = MaterialTheme.typography.bodySmall)
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Dhaka, Banani", color = Color.White, style = MaterialTheme.typography.labelLarge)
                                    CoffeeIcon(Glyph.DOWN, Modifier.size(15.dp), Color.White)
                                }
                            }
                            IconButton(onProfile, Modifier.size(44.dp).background(CoffeeColors.Cream, RoundedCornerShape(14.dp))) {
                                CoffeeIcon(Glyph.COFFEE, tint = CoffeeColors.Copper, description = "Your coffee profile")
                            }
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(query, { query = it }, Modifier.weight(1f).heightIn(min = 54.dp),
                                placeholder = { Text("Search coffee", fontSize = 13.sp, color = Color(0xFFB0B0B0)) },
                                leadingIcon = { CoffeeIcon(Glyph.SEARCH, Modifier.size(20.dp), Color.White) },
                                trailingIcon = if (query.isNotEmpty()) ({ IconButton({ query = "" }) { CoffeeIcon(Glyph.CLOSE, Modifier.size(16.dp), Color.White, "Clear search") } }) else null,
                                singleLine = true, shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                                    focusedContainerColor = Color(0xFF2A2A2A), unfocusedContainerColor = Color(0xFF2A2A2A),
                                    focusedBorderColor = CoffeeColors.Copper, unfocusedBorderColor = Color.Transparent))
                            FilledIconButton({ focusManager.clearFocus(); filters = true }, Modifier.size(54.dp), shape = RoundedCornerShape(14.dp)) {
                                CoffeeIcon(Glyph.FILTER, tint = Color.White, description = "Filter coffees")
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        PromotionCard(onOffer)
                    }
                }
                LazyRow(Modifier.padding(top = 22.dp), contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(CoffeeCatalog.categories) { name ->
                        FilterChip(selected = category == name, onClick = { category = name; focusManager.clearFocus() }, label = { Text(name) },
                            modifier = Modifier.semantics { contentDescription = "Category $name" },
                            shape = RoundedCornerShape(9.dp), border = null,
                            colors = FilterChipDefaults.filterChipColors(containerColor = Color(0xFFF0F0F0),
                                selectedContainerColor = CoffeeColors.Copper, selectedLabelColor = Color.White))
                    }
                }
                Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(if (query.isBlank()) "Made for your moment" else "Search results", style = MaterialTheme.typography.titleMedium)
                    Text("${coffees.size} ${if (coffees.size == 1) "coffee" else "coffees"}", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        if (coffees.isEmpty()) item(span = { GridItemSpan(maxLineSpan) }) {
            EmptyCoffeeState("No coffee found", "Try another name, category, or clear your filters.", Glyph.SEARCH, "Reset search", {
                query = ""; category = "All Coffee"; affordableOnly = false; focusManager.clearFocus()
            }, Modifier.height(340.dp))
        }
        itemsIndexed(coffees, key = { _, coffee -> coffee.id }) { index, coffee ->
            val columns = 2
            // Grid padding is per item so the full-bleed header can extend edge to edge.
            Box(Modifier.padding(start = if (index % columns == 0) 24.dp else 0.dp, end = if (index % columns == columns - 1) 24.dp else 0.dp)) {
                CoffeeCard(coffee, { onCoffee(coffee.id) }, { onAdd(coffee.id) })
            }
        }
    }
    if (filters) AlertDialog(onDismissRequest = { filters = false }, title = { Text("Your perfect cup") }, text = {
        Column {
            Text("Sort by", style = MaterialTheme.typography.titleMedium)
            listOf(CoffeeSort.RECOMMENDED to "Recommended", CoffeeSort.PRICE_LOW to "Price: low to high", CoffeeSort.PRICE_HIGH to "Price: high to low").forEach { (value, label) ->
                Row(Modifier.fillMaxWidth().clickable { sorting = value }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(sorting == value, { sorting = value }); Text(label)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(affordableOnly, { affordableOnly = it }); Text("Coffees under $4") }
        }
    }, confirmButton = { TextButton({ filters = false }) { Text("Show coffees") } }, dismissButton = {
        TextButton({ sorting = CoffeeSort.RECOMMENDED; affordableOnly = false }) { Text("Reset") }
    })
}

@Composable
fun PromotionCard(onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick)) {
        Image(painterResource(R.drawable.coffee_promo), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFF73503A).copy(alpha = .65f), Color.Transparent))))
        Column(Modifier.padding(16.dp)) {
            Text("Promo", Modifier.background(Color(0xFFED5151), RoundedCornerShape(6.dp)).padding(horizontal = 7.dp, vertical = 3.dp),
                color = Color.White, fontSize = 12.sp, lineHeight = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(7.dp))
            Text("Buy one get\none FREE", color = Color.White, fontSize = 27.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold, letterSpacing = (-.8).sp)
            Text("A little more to love.", color = Color.White.copy(alpha = .85f), fontSize = 10.sp, lineHeight = 12.sp)
        }
    }
}
