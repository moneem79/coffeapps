package com.example.project001coffeeshop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.PathParser
import com.example.project001coffeeshop.model.Coffee
import com.example.project001coffeeshop.model.money
import com.example.project001coffeeshop.ui.theme.CoffeeColors

enum class Glyph(val path: String) {
    HOME("M3 10.5L12 3l9 7.5V21h-6v-7H9v7H3Z"),
    HEART("M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8L12 21l8.8-8.6a5.5 5.5 0 0 0 0-7.8Z"),
    BAG("M5 8h14l2 13H3ZM8 8V6a4 4 0 0 1 8 0v2"),
    BELL("M18 8a6 6 0 0 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4"),
    SEARCH("M20.5 20.5l-5-5M17 10a7 7 0 1 1-14 0a7 7 0 0 1 14 0"),
    FILTER("M3 6h5m5 0h8M3 18h10m5 0h3M8 3v6m5 6v6M8 6a2 2 0 1 0 4 0a2 2 0 0 0-4 0M13 18a2 2 0 1 0 4 0a2 2 0 0 0-4 0"),
    BACK("M15 5l-7 7 7 7"), NEXT("M9 5l7 7-7 7"), DOWN("M6 9l6 6 6-6"),
    PLUS("M12 5v14M5 12h14"), MINUS("M5 12h14"), CLOSE("M6 6l12 12M18 6L6 18"),
    COFFEE("M4 9h13v7a5 5 0 0 1-5 5H9a5 5 0 0 1-5-5ZM17 10h2a3 3 0 0 1 0 6h-2M7 2v3m4-3v3m4-3v3"),
    BEAN("M7 4c6-5 17 4 12 12S2 23 3 13C3 9 4 6 7 4ZM16 4c-7 5-1 10-10 16"),
    MILK("M8 3h8v4l3 4v10H5V11l3-4ZM8 7h8M5 12h14M9 16h6"),
    STAR("M12 2l3.1 6.3 7 .9-5.1 5 1.2 7-6.2-3.3-6.2 3.3 1.2-7-5.1-5 7-.9Z"),
    LOCATION("M19 10c0 6-7 12-7 12S5 16 5 10a7 7 0 1 1 14 0ZM15 10a3 3 0 1 1-6 0a3 3 0 0 1 6 0"),
    CHECK("M5 12l4 4L20 5"), CLOCK("M12 7v5l4 2M22 12a10 10 0 1 1-20 0a10 10 0 0 1 20 0"),
    WALLET("M3 6h16v15H3ZM3 6V3h14v3M16 11h6v5h-6Z"),
    EDIT("M14 4l6 6M3 21l5-1L21 7l-5-5L3 15ZM13 21h8"),
    NOTE("M5 3h14v18H5ZM8 8h8M8 12h8M8 16h5"),
    DELIVERY("M2 5h12v12H2ZM14 9h4l4 5v3h-8M8 19a2 2 0 1 1-4 0a2 2 0 0 1 4 0M20 19a2 2 0 1 1-4 0a2 2 0 0 1 4 0"),
    PHONE("M5 3h4l2 5-3 2a15 15 0 0 0 6 6l2-3 5 2v4c-10 4-22-12-16-16Z"),
    USER("M16 7a4 4 0 1 1-8 0a4 4 0 0 1 8 0M4 21v-3a8 8 0 0 1 16 0v3"),
}

@Composable
fun CoffeeIcon(glyph: Glyph, modifier: Modifier = Modifier, tint: Color = CoffeeColors.Ink,
    description: String? = null, filled: Boolean = false) {
    val path = remember(glyph) { requireNotNull(PathParser.createPathFromPathData(glyph.path)).asComposePath() }
    Canvas(modifier.size(24.dp).then(if (description == null) Modifier else Modifier.semantics { contentDescription = description })) {
        withTransform({ scale(size.width / 24f, size.height / 24f, Offset.Zero) }) {
            if (filled) drawPath(path, tint) else drawPath(path, tint, style = Stroke(1.7f, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

@Composable
fun CoffeePhoto(resource: Int, description: String?, modifier: Modifier = Modifier) {
    Image(painterResource(resource), description, modifier.clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop)
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick, modifier.heightIn(min = 56.dp), enabled = enabled, shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)) {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun ScreenHeader(title: String, onBack: () -> Unit, action: (@Composable () -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onBack) { CoffeeIcon(Glyph.BACK, description = "Back") }
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) { action?.invoke() }
    }
}

@Composable
fun Brand(modifier: Modifier = Modifier, light: Boolean = false) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        Box(Modifier.size(34.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
            CoffeeIcon(Glyph.COFFEE, Modifier.size(21.dp), CoffeeColors.Copper)
        }
        Text("JavaGem", color = if (light) Color.White else CoffeeColors.Ink, fontWeight = FontWeight.SemiBold, fontSize = 19.sp, letterSpacing = (-0.5).sp)
    }
}

@Composable
fun CoffeeCard(coffee: Coffee, onOpen: () -> Unit, onAdd: () -> Unit) {
    Surface(onClick = onOpen, shape = RoundedCornerShape(18.dp), color = Color.White) {
        Column(Modifier.padding(8.dp)) {
            Box {
                CoffeePhoto(coffee.image, coffee.name, Modifier.fillMaxWidth().height(142.dp))
                Row(Modifier.align(Alignment.TopEnd).padding(6.dp).background(Color.Black.copy(alpha = .55f), RoundedCornerShape(9.dp))
                    .padding(horizontal = 6.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    CoffeeIcon(Glyph.STAR, Modifier.size(10.dp), Color(0xFFFBBE21), filled = true)
                    Text(coffee.rating, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Text(coffee.name, Modifier.padding(start = 4.dp, top = 10.dp), style = MaterialTheme.typography.titleMedium,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(coffee.subtitle, Modifier.padding(start = 4.dp, top = 2.dp), color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth().padding(start = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(money(coffee.priceCents), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                IconButton(onAdd, Modifier.size(48.dp)) {
                    Box(Modifier.size(34.dp).background(CoffeeColors.Copper, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                        CoffeeIcon(Glyph.PLUS, Modifier.size(18.dp), Color.White, "Add ${coffee.name}")
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyCoffeeState(title: String, subtitle: String, glyph: Glyph, actionText: String, onAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(100.dp).background(CoffeeColors.Cream, CircleShape), contentAlignment = Alignment.Center) {
            CoffeeIcon(glyph, Modifier.size(42.dp), CoffeeColors.Copper)
        }
        Spacer(Modifier.height(24.dp))
        Text(title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        Text(subtitle, color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(28.dp))
        PrimaryButton(actionText, onClick = onAction)
    }
}

@Composable
fun BottomNavigation(route: String, bagCount: Int, onNavigate: (String) -> Unit) {
    Surface(shadowElevation = 12.dp, shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp), color = Color.White) {
        Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 20.dp).height(74.dp), horizontalArrangement = Arrangement.SpaceAround) {
            listOf(Triple("home", Glyph.HOME, "Home"), Triple("favorites", Glyph.HEART, "Favorites"),
                Triple("cart", Glyph.BAG, "Bag"), Triple("updates", Glyph.BELL, "Updates")).forEach { (destination, icon, label) ->
                val selected = destination == route
                Column(Modifier.weight(1f).fillMaxHeight().clickable { onNavigate(destination) }.semantics { contentDescription = label },
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Box {
                        CoffeeIcon(icon, tint = if (selected) CoffeeColors.Copper else CoffeeColors.Muted, filled = selected && icon == Glyph.HOME)
                        if (destination == "cart" && bagCount > 0) {
                            Box(Modifier.align(Alignment.TopEnd).offset(9.dp, (-7).dp).size(17.dp).background(CoffeeColors.Copper, CircleShape), contentAlignment = Alignment.Center) {
                                Text(if (bagCount > 9) "9+" else "$bagCount", fontSize = 9.sp, color = Color.White)
                            }
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                    Box(Modifier.width(if (selected) 10.dp else 4.dp).height(4.dp).background(if (selected) CoffeeColors.Copper else Color.Transparent, CircleShape))
                }
            }
        }
    }
}
