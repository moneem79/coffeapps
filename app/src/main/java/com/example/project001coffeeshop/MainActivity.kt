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
import com.example.project001coffeeshop.model.AuthPage
import com.example.project001coffeeshop.ui.AuthViewModel
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
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        setContent {
            Project001coffeeshopTheme {
                JavaGemApp(viewModel, authViewModel) { darkHeader, lightNavigation ->
                    val controller = WindowCompat.getInsetsController(window, window.decorView)
                    controller.isAppearanceLightStatusBars = !darkHeader
                    controller.isAppearanceLightNavigationBars = lightNavigation
                }
            }
        }
    }
}

@Composable
private fun JavaGemApp(viewModel: CoffeeViewModel, authViewModel: AuthViewModel, onSystemBars: (Boolean, Boolean) -> Unit) {
    val state by viewModel.state.collectAsState()
    val auth by authViewModel.state.collectAsState()
    // A stored shopping/onboarding flag must never bypass the entry flow.
    val atAuth = auth.profile == null
    var route by rememberSaveable { mutableStateOf("home") }
    var detailReturn by rememberSaveable { mutableStateOf("home") }
    var dialog by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbars = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) { viewModel.events.collect { snackbars.showSnackbar(it, duration = SnackbarDuration.Short) } }
    SideEffect { onSystemBars(!atAuth && route == "home", true) }
    LaunchedEffect(atAuth) { if (atAuth) dialog = null }
    fun authBack() {
        if (auth.page != AuthPage.SIGN_IN) authViewModel.open(AuthPage.SIGN_IN)
    }
    BackHandler(if (atAuth) auth.page != AuthPage.SIGN_IN else route != "home") {
        if (atAuth) authBack() else route = if (route.startsWith("detail:")) detailReturn else "home"
    }
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
                    if (!atAuth && route in listOf("home", "favorites", "updates")) {
                        BottomNavigation(route, state.cart.sumOf { it.quantity }) { route = it }
                    }
                }) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) {
                    if (atAuth) AuthScreen(auth, ::authBack, authViewModel::open, authViewModel::name,
                        authViewModel::email, authViewModel::password, authViewModel::confirm,
                        onSubmit = {
                            if (authViewModel.submit()) {
                                viewModel.start(); route = "home"
                                viewModel.message("Demo profile ready. No credentials were verified.")
                            }
                        })
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
    if (!atAuth && (dialog == "address" || dialog == "note")) {
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
    if (!atAuth && dialog == "offer") AlertDialog(onDismissRequest = { dialog = null }, title = { Text("Good coffee is better shared.") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PromotionCard { }
            Text("Add any two coffees and the lower-priced one is on us. The offer repeats for each pair. Apply JAVAGEM in your bag.", style = MaterialTheme.typography.bodyMedium)
            Text("Local frontend demo offer · no real purchase", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodySmall)
        } }, confirmButton = {
            TextButton({ if (!state.offerApplied) viewModel.applyOffer(); dialog = null; route = if (state.cart.isEmpty()) "home" else "cart" }) {
                Text(if (state.offerApplied) "Continue shopping" else "Apply JAVAGEM")
            }
        }, dismissButton = { TextButton({ dialog = null }) { Text("Maybe later") } })
    if (!atAuth && dialog == "profile") AlertDialog(onDismissRequest = { dialog = null }, title = { Brand() }, text = {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(auth.profile?.name.orEmpty(), style = MaterialTheme.typography.titleLarge)
            auth.profile?.let { profile ->
                Text(profile.email, style = MaterialTheme.typography.bodyMedium)
                Text("Demo profile · not authenticated", color = CoffeeColors.Copper, style = MaterialTheme.typography.bodySmall)
            }
            Text("This is a session-only profile preview. No real account is connected.", color = CoffeeColors.Muted, style = MaterialTheme.typography.bodyMedium)
            Text("${state.favorites.size} favorites · ${state.cart.sumOf { it.quantity }} coffees in your bag")
            TextButton({ dialog = null; route = "home"; authViewModel.signOut() }) { Text("Sign out of demo") }
            TextButton({ dialog = "address" }) { Text("Edit delivery address") }
            TextButton({ dialog = null; route = "updates" }) { Text("View your latest demo order") }
        }
    }, confirmButton = { TextButton({ dialog = null }) { Text("Lovely") } })
    state.recoveryMessage?.takeIf { !atAuth }?.let { message ->
        AlertDialog(onDismissRequest = viewModel::dismissRecovery, title = { Text("Welcome back") }, text = { Text(message) },
            confirmButton = { TextButton(viewModel::dismissRecovery) { Text("Continue") } })
    }
}
