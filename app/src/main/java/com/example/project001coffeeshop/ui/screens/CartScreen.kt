package com.example.project001coffeeshop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.project001coffeeshop.data.CoffeeCatalog
import com.example.project001coffeeshop.model.*
import com.example.project001coffeeshop.ui.components.*
import com.example.project001coffeeshop.ui.theme.CoffeeColors

@Composable
fun CartScreen(state: CoffeeState, onBack: () -> Unit, onExplore: () -> Unit, onPickup: (Boolean) -> Unit,
    onQuantity: (String, Int) -> Unit, onAddress: () -> Unit, onNote: () -> Unit, onOffer: () -> Unit,
    onPayment: (String) -> Unit, onOrder: () -> Unit) {
    var paymentOpen by remember { mutableStateOf(false) }
    val price = CoffeePricing.summarize(state.cart, CoffeeCatalog.coffees, state.offerApplied, state.pickup)
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Order", onBack)
        if (state.cart.isEmpty()) {
            EmptyCoffeeState("Your bag is taking a break", "Find your favorite coffee and we'll make it just right.", Glyph.BAG, "Explore coffee", onExplore)
        } else {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                item {
                    Row(Modifier.fillMaxWidth().background(Color(0xFFEDEDED), RoundedCornerShape(14.dp)).padding(4.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SelectOption("Deliver", !state.pickup, Modifier.weight(1f)) { onPickup(false) }
                        SelectOption("Pick up", state.pickup, Modifier.weight(1f)) { onPickup(true) }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(if (state.pickup) "Pick-up location" else "Delivery Address", style = MaterialTheme.typography.titleMedium)
                        Text(if (state.pickup) "JavaGem Coffee" else "Banani, Dhaka", style = MaterialTheme.typography.labelLarge)
                        Text(if (state.pickup) "Road 11, Banani · Open until 10 PM (demo)" else state.address, color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!state.pickup) OutlinedButton(onAddress, contentPadding = PaddingValues(horizontal = 10.dp), modifier = Modifier.heightIn(min = 44.dp), border = androidx.compose.foundation.BorderStroke(1.dp, CoffeeColors.Border)) {
                                CoffeeIcon(Glyph.EDIT, Modifier.size(14.dp)); Spacer(Modifier.width(5.dp)); Text("Edit Address", style = MaterialTheme.typography.bodySmall)
                            }
                            OutlinedButton(onNote, contentPadding = PaddingValues(horizontal = 10.dp), modifier = Modifier.heightIn(min = 44.dp), border = androidx.compose.foundation.BorderStroke(1.dp, CoffeeColors.Border)) {
                                CoffeeIcon(Glyph.NOTE, Modifier.size(14.dp)); Spacer(Modifier.width(5.dp)); Text(if (state.deliveryNote.isBlank()) "Add Note" else "Edit Note", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        if (state.deliveryNote.isNotBlank()) Text(state.deliveryNote, style = MaterialTheme.typography.bodySmall, color = CoffeeColors.Copper)
                    }
                }
                item { HorizontalDivider(color = CoffeeColors.Border) }
                items(state.cart, key = { it.key }) { line ->
                    val coffee = CoffeeCatalog.find(line.coffeeId)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CoffeePhoto(coffee.image, coffee.name, Modifier.size(66.dp))
                        Column(Modifier.weight(1f)) {
                            Text(coffee.name, style = MaterialTheme.typography.titleMedium)
                            Text("${line.size.label} · ${line.temperature.label}", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
                            Text(money(CoffeePricing.unitPrice(coffee, line.size)), style = MaterialTheme.typography.labelLarge)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            QuantityButton(Glyph.MINUS, "Decrease ${coffee.name}") { onQuantity(line.key, -1) }
                            Text("${line.quantity}", Modifier.padding(horizontal = 6.dp), style = MaterialTheme.typography.labelLarge)
                            QuantityButton(Glyph.PLUS, "Increase ${coffee.name}", line.quantity < 20) { onQuantity(line.key, 1) }
                        }
                    }
                }
                item {
                    Surface(onClick = onOffer, shape = RoundedCornerShape(14.dp), border = androidx.compose.foundation.BorderStroke(1.dp, CoffeeColors.Border)) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            CoffeeIcon(if (state.offerApplied) Glyph.CHECK else Glyph.STAR, Modifier.size(20.dp), CoffeeColors.Copper)
                            Column(Modifier.weight(1f)) {
                                Text(if (state.offerApplied) "JAVAGEM offer applied" else "Buy one, get one free", style = MaterialTheme.typography.labelLarge)
                                Text(if (state.offerApplied) "Tap to remove · lower-priced drink is free" else "Tap to apply your welcome offer", style = MaterialTheme.typography.bodySmall, color = CoffeeColors.Muted)
                            }
                            CoffeeIcon(Glyph.NEXT, Modifier.size(18.dp))
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Payment Summary", style = MaterialTheme.typography.titleMedium)
                        SummaryRow("Price", money(price.subtotal))
                        if (price.discount > 0) SummaryRow("JAVAGEM savings", "− ${money(price.discount)}", CoffeeColors.Green)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (state.pickup) "Pick-up" else "Delivery Fee", style = MaterialTheme.typography.bodyMedium)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (!state.pickup) Text("$ 2.00", textDecoration = TextDecoration.LineThrough, color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                                Text(if (state.pickup) "Free" else money(price.delivery), style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        HorizontalDivider(color = CoffeeColors.Border)
                        SummaryRow("Total", money(price.total))
                    }
                }
                item {
                    Text("Demo checkout · No payment is collected and no real order is sent.", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
            Surface(color = Color.White, shadowElevation = 10.dp, shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)) {
                Column(Modifier.navigationBarsPadding().padding(horizontal = 24.dp).padding(top = 12.dp, bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth().clickable { paymentOpen = true }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        CoffeeIcon(Glyph.WALLET, tint = CoffeeColors.Copper)
                        Column(Modifier.weight(1f)) {
                            Text(state.payment, style = MaterialTheme.typography.labelLarge)
                            Text(money(price.total), color = CoffeeColors.Copper, style = MaterialTheme.typography.bodyMedium)
                        }
                        CoffeeIcon(Glyph.DOWN, Modifier.size(18.dp))
                    }
                    PrimaryButton("Place demo order", Modifier.fillMaxWidth(), onClick = onOrder)
                }
            }
        }
    }
    if (paymentOpen) AlertDialog(onDismissRequest = { paymentOpen = false }, title = { Text("Payment method") }, text = {
        Column {
            Text("Preview only. No card or wallet will be charged.", style = MaterialTheme.typography.bodyMedium, color = CoffeeColors.Muted)
            listOf("Cash on delivery", "Wallet (demo)").forEach { payment ->
                Row(Modifier.fillMaxWidth().clickable { onPayment(payment); paymentOpen = false }, verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(state.payment == payment, { onPayment(payment); paymentOpen = false }); Text(payment)
                }
            }
        }
    }, confirmButton = { TextButton({ paymentOpen = false }) { Text("Done") } })
}

@Composable
private fun QuantityButton(icon: Glyph, description: String, enabled: Boolean = true, onClick: () -> Unit) {
    IconButton(onClick, Modifier.size(48.dp), enabled = enabled) {
        Box(Modifier.size(28.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
            CoffeeIcon(icon, Modifier.size(14.dp), if (enabled) CoffeeColors.Ink else CoffeeColors.Border, description)
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, color: Color = CoffeeColors.Ink) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.labelLarge, color = color)
    }
}
