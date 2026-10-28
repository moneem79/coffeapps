package com.example.project001coffeeshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.example.project001coffeeshop.data.CoffeeCatalog
import com.example.project001coffeeshop.ui.CoffeeViewModel
import com.example.project001coffeeshop.ui.components.BottomNavigation
import com.example.project001coffeeshop.ui.components.Brand
import com.example.project001coffeeshop.ui.screens.*
import com.example.project001coffeeshop.ui.theme.CoffeeColors
import com.example.project001coffeeshop.ui.theme.Project001coffeeshopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel = ViewModelProvider(this)[CoffeeViewModel::class.java]
        setContent {
            Project001coffeeshopTheme {
                JavaGemApp(viewModel) { darkHeader, lightNavigation ->
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    controller.isAppearanceLightStatusBars = !darkHeader
                    controller.isAppearanceLightNavigationBars = lightNavigation
                }
            }
        }
    }
}

@Composable
private fun JavaGemApp(viewModel: CoffeeViewModel, onSystemBars: (Boolean, Boolean) -> Unit) {
    val state by viewModel.state.collectAsState()
    var route by rememberSaveable { mutableStateOf("home") }
    var detailReturn by rememberSaveable { mutableStateOf("home") }
    var dialog by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbars = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) { viewModel.events.collect { snackbars.showSnackbar(it, duration = SnackbarDuration.Short) } }
    SideEffect { onSystemBars(!state.started || route == "home", state.started) }
    BackHandler(state.started && route != "home") { route = if (route.startsWith("detail:")) detailReturn else "home" }
    fun openCoffee(id: String) { detailReturn = if (route == "favorites") "favorites" else "home"; route = "detail:$id" }

    Box(Modifier.fillMaxSize().background(CoffeeColors.Background), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = 640.dp).fillMaxWidth().fillMaxHeight()) {
            Scaffold(containerColor = CoffeeColors.Background, contentWindowInsets = WindowInsets(0, 0, 0, 0),
                snackbarHost = {
                    val clearance = when {
                        route == "cart" -> 174.dp
                        route.startsWith("detail:") -> 124.dp
                        route == "tracking" -> 90.dp
                        else -> 0.dp
                    }
                    SnackbarHost(snackbars, Modifier.navigationBarsPadding().padding(bottom = clearance))
                },
                bottomBar = {
                    if (state.started && route in listOf("home", "favorites", "updates")) {
                        BottomNavigation(route, state.cart.sumOf { it.quantity }) { route = it }
                    }
                }) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    if (!state.started) WelcomeScreen { viewModel.start(); route = "home" }
                    else when {
                        route.startsWith("detail:") -> {
                            val coffee = CoffeeCatalog.find(route.substringAfter(":"))
                            DetailScreen(coffee, coffee.id in state.favorites, { route = detailReturn }, { viewModel.favorite(coffee.id) }) { size, temperature ->
                                viewModel.addToCart(coffee.id, size, temperature); route = "cart"
                            }
                        }
                        route == "favorites" -> FavoritesScreen(state.favorites, ::openCoffee, { viewModel.addToCart(it) }, { route = "home" })
                        route == "cart" -> CartScreen(state, { route = "home" }, { route = "home" }, viewModel::pickup,
                            viewModel::quantity, { dialog = "address" }, { dialog = "note" }, viewModel::applyOffer, viewModel::payment,
                            { if (viewModel.placeDemoOrder()) route = "tracking" })
                        route == "tracking" -> TrackingScreen(state.order, { route = "updates" }, viewModel::advanceOrder, { route = "home" })
                        route == "updates" -> UpdatesScreen(state.order, { route = "tracking" }, { dialog = "offer" }, { route = "home" })
                        else -> HomeScreen(::openCoffee, { viewModel.addToCart(it) }, { dialog = "address" }, { dialog = "profile" }, { dialog = "offer" })
                    }
                }
            }
        }
    }
    if (dialog == "address" || dialog == "note") {
        val isAddress = dialog == "address"
        var text by rememberSaveable(dialog) { mutableStateOf(if (isAddress) state.address else state.deliveryNote) }
        AlertDialog(onDismissRequest = { dialog = null }, title = { Text(if (isAddress) "Delivery address" else "A note for your order") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (isAddress) "Set an address for your demo order. The store location is Banani, Dhaka." else "Anything you'd like your barista to know?", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
                    OutlinedTextField(text, { text = it.take(400) }, Modifier.fillMaxWidth(),
                        label = { Text(if (isAddress) "Street, building, area" else "Order note") }, minLines = 2, maxLines = 5)
                }
            }, confirmButton = {
                TextButton({ if (isAddress) viewModel.address(text) else viewModel.note(text); dialog = null }, enabled = !isAddress || text.isNotBlank()) { Text("Save") }
            }, dismissButton = { TextButton({ dialog = null }) { Text("Cancel") } })
    }
    if (dialog == "offer") AlertDialog(onDismissRequest = { dialog = null }, title = { Text("Good coffee is better shared.") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PromotionCard { }
            Text("Add any two coffees and the lower-priced one is on us. The offer repeats for each pair. Apply JAVAGEM in your bag.", style = MaterialTheme.typography.bodyMedium)
            Text("Local frontend demo offer · no real purchase", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
        } }, confirmButton = {
            TextButton({ if (!state.offerApplied) viewModel.applyOffer(); dialog = null; route = if (state.cart.isEmpty()) "home" else "cart" }) {
                Text(if (state.offerApplied) "Continue shopping" else "Apply JAVAGEM")
            }
        }, dismissButton = { TextButton({ dialog = null }) { Text("Maybe later") } })
    if (dialog == "profile") AlertDialog(onDismissRequest = { dialog = null }, title = { Brand() }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Your little coffee corner", style = MaterialTheme.typography.titleLarge)
            Text("You're browsing as a guest. Your bag and favorites stay on this device.", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
            Text("${state.favorites.size} favorites · ${state.cart.sumOf { it.quantity }} coffees in your bag")
            TextButton({ dialog = "address" }) { Text("Edit delivery address") }
            TextButton({ dialog = null; route = "updates" }) { Text("View your latest demo order") }
            TextButton({ dialog = null; route = "home"; viewModel.replayWelcome() }) { Text("Replay welcome screen") }
        }
    }, confirmButton = { TextButton({ dialog = null }) { Text("Lovely") } })
    state.recoveryMessage?.let { message ->
        AlertDialog(onDismissRequest = viewModel::dismissRecovery, title = { Text("Welcome back") }, text = { Text(message) },
            confirmButton = { TextButton(viewModel::dismissRecovery) { Text("Continue") } })
    }
}
