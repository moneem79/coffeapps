package com.example.project001coffeeshop.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project001coffeeshop.model.DemoOrder
import com.example.project001coffeeshop.model.money
import com.example.project001coffeeshop.ui.components.*
import com.example.project001coffeeshop.ui.theme.CoffeeColors

@Composable
fun TrackingScreen(order: DemoOrder?, onBack: () -> Unit, onAdvance: () -> Unit, onExplore: () -> Unit) {
    var courierInfo by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().navigationBarsPadding()) {
        ScreenHeader("Your coffee journey", onBack)
        if (order == null) EmptyCoffeeState("Your next cup awaits", "Place a demo order to explore the delivery experience.", Glyph.COFFEE, "Explore coffee", onExplore)
        else Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(bottom = 24.dp)) {
            RouteIllustration(Modifier.fillMaxWidth().height(245.dp))
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(when { order.stage == 3 -> "Made for a happy moment"; order.pickup -> "Ready in 12–18 minutes"; else -> "12–18 minutes left" }, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Text(if (order.pickup) "Pick up at JavaGem Coffee" else "Delivery to ${order.address}", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    repeat(4) { index -> Box(Modifier.weight(1f).height(5.dp).background(if (index <= order.stage) CoffeeColors.Green else CoffeeColors.Border, CircleShape)) }
                }
                Surface(shape = RoundedCornerShape(16.dp), border = androidx.compose.foundation.BorderStroke(1.dp, CoffeeColors.Border), color = Color.White) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        CoffeeIcon(if (order.stage == 3) Glyph.CHECK else Glyph.DELIVERY, Modifier.size(32.dp), CoffeeColors.Copper)
                        Column(Modifier.weight(1f)) {
                            Text(listOf("Order received", "Brewing your coffee", if (order.pickup) "Ready for collection" else "On the way", if (order.pickup) "Collected. Enjoy!" else "Delivered. Enjoy!")[order.stage], style = MaterialTheme.typography.titleMedium)
                            Text("${order.id} · ${order.itemCount} coffees · ${money(order.totalCents)}", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                if (!order.pickup && order.stage < 3) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(52.dp).background(CoffeeColors.Cream, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                        Text("AR", color = CoffeeColors.Copper, fontWeight = FontWeight.SemiBold)
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Alex Rahman", style = MaterialTheme.typography.titleMedium)
                        Text("Your demo courier", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                    }
                    OutlinedIconButton({ courierInfo = true }) { CoffeeIcon(Glyph.PHONE, tint = CoffeeColors.Copper, description = "Courier information") }
                }
                Text("Simulated order and illustrated route. No real delivery or live location.", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                PrimaryButton(if (order.stage == 3) "Find your next favorite" else "Advance demo ${if (order.pickup) "pickup" else "delivery"}", Modifier.fillMaxWidth()) {
                    if (order.stage == 3) onExplore() else onAdvance()
                }
            }
        }
    }
    if (courierInfo) AlertDialog(onDismissRequest = { courierInfo = false }, title = { Text("Meet your demo courier") },
        text = { Text("Alex is part of this frontend preview. No phone call is placed and no courier is dispatched.") },
        confirmButton = { TextButton({ courierInfo = false }) { Text("Got it") } })
}

@Composable
private fun RouteIllustration(modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFFF0F1EE))) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            for (row in 0..3) for (column in 0..5) {
                drawRoundRect(if ((row + column) % 5 == 0) Color(0xFFDDE8D9) else Color(0xFFE4E5E1),
                    Offset(column * w / 5 + 8, row * h / 3 + 8), Size(w / 5 - 22, h / 3 - 22), CornerRadius(7f))
            }
            repeat(5) { drawLine(Color.White, Offset(it * w / 4f, 0f), Offset(it * w / 4f, h), 11f) }
            repeat(4) { drawLine(Color.White, Offset(0f, it * h / 3f), Offset(w, it * h / 3f), 11f) }
            val route = Path().apply { moveTo(w * .23f, h * .75f); lineTo(w * .23f, h * .34f); lineTo(w * .73f, h * .34f); lineTo(w * .73f, h * .62f) }
            drawPath(route, CoffeeColors.Copper, style = Stroke(10f, cap = StrokeCap.Round))
            listOf(Offset(w * .23f, h * .75f), Offset(w * .73f, h * .62f)).forEach {
                drawCircle(Color.White, 21f, it); drawCircle(CoffeeColors.Copper, 12f, it)
            }
        }
        Surface(Modifier.align(Alignment.TopCenter).padding(14.dp), shape = RoundedCornerShape(20.dp), color = Color.White) {
            Text("Illustrated route · demo", Modifier.padding(horizontal = 13.dp, vertical = 7.dp), fontSize = 11.sp, color = CoffeeColors.Muted)
        }
        Surface(Modifier.align(Alignment.BottomStart).padding(16.dp), shape = RoundedCornerShape(12.dp), color = Color.White) {
            Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                CoffeeIcon(Glyph.COFFEE, Modifier.size(17.dp), CoffeeColors.Copper); Text("JavaGem", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
